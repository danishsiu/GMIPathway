package com.week14.gmipathway.ui.news;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.week14.gmipathway.data.NewsRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Loads the news off the main thread and keeps it when the screen rotates,
 * so turning the phone does not download it again.
 */
public class NewsViewModel extends AndroidViewModel {

    /** What the news screen should show. */
    static final class State {
        final boolean loading;
        @Nullable
        final NewsRepository.Result result;
        final boolean failed;

        private State(boolean loading, @Nullable NewsRepository.Result result, boolean failed) {
            this.loading = loading;
            this.result = result;
            this.failed = failed;
        }
    }

    private final NewsRepository repository;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final MutableLiveData<State> state = new MutableLiveData<>();

    public NewsViewModel(@NonNull Application application) {
        super(application);
        repository = new NewsRepository(application);
    }

    LiveData<State> getState() {
        return state;
    }

    /** Loads the news the first time only. */
    void loadIfNeeded() {
        if (state.getValue() == null) {
            refresh();
        }
    }

    void refresh() {
        State current = state.getValue();
        if (current != null && current.loading) {
            return;
        }
        // Keep the old list on screen while the new one downloads.
        state.setValue(new State(true, current != null ? current.result : null, false));
        executor.execute(() -> {
            try {
                state.postValue(new State(false, repository.load(), false));
            } catch (Exception e) {
                state.postValue(new State(false, null, true));
            }
        });
    }

    @Override
    protected void onCleared() {
        executor.shutdownNow();
    }
}
