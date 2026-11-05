package com.week14.gmipathway.ui.enquiry;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;

import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.week14.gmipathway.R;
import com.week14.gmipathway.data.CourseRepository;
import com.week14.gmipathway.model.Course;
import com.week14.gmipathway.ui.common.BaseActivity;
import com.week14.gmipathway.ui.common.ExternalLinks;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Checks the enquiry details, then opens the email app with the message ready to send to GMI.
 */
public class EnquiryFormActivity extends BaseActivity {
    private static final String STATE_PROGRAMME = "programme";
    private static final String STATE_HANDED_OFF = "handedOff";
    private static final String EXTRA_PROGRAMME = "programmeName";

    /** Opens the form with a programme already chosen, for example from its course page. */
    public static Intent createIntent(Context context, String programmeName) {
        return new Intent(context, EnquiryFormActivity.class)
                .putExtra(EXTRA_PROGRAMME, programmeName);
    }

    /** Digits with an optional leading plus, spaces or hyphens; 9 to 15 digits in total. */
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9][0-9 -]{7,17}[0-9]$");

    private TextInputLayout nameLayout;
    private TextInputLayout emailLayout;
    private TextInputLayout phoneLayout;
    private TextInputLayout programmeLayout;
    private TextInputLayout messageLayout;
    private TextInputEditText nameInput;
    private TextInputEditText emailInput;
    private TextInputEditText phoneInput;
    private TextInputEditText messageInput;
    private MaterialAutoCompleteTextView programmeInput;

    /** The dropdown is rebuilt after rotation, so its value is kept here. */
    private String selectedProgramme;

    /** True while the email app is open with this enquiry, so we can confirm on return. */
    private boolean handedOff;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enquiry_form);
        setUpToolbar(R.string.enquiry_title);
        applyWindowInsets();

        bindViews();
        if (savedInstanceState != null) {
            selectedProgramme = savedInstanceState.getString(STATE_PROGRAMME);
            handedOff = savedInstanceState.getBoolean(STATE_HANDED_OFF);
        } else {
            // Opened from a course page: that programme is already chosen.
            selectedProgramme = getIntent().getStringExtra(EXTRA_PROGRAMME);
        }
        setUpProgrammeDropdown();
        findViewById(R.id.button_submit).setOnClickListener(view -> submit());
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_PROGRAMME, selectedProgramme);
        outState.putBoolean(STATE_HANDED_OFF, handedOff);
    }

    /** Back from the email app: say what happens next. The form keeps what was typed. */
    @Override
    protected void onResume() {
        super.onResume();
        if (!handedOff) {
            return;
        }
        handedOff = false;
        new MaterialAlertDialogBuilder(this)
                .setIcon(R.drawable.ic_mail)
                .setTitle(R.string.enquiry_sent_title)
                .setMessage(getString(R.string.enquiry_sent_body, textOf(emailInput)))
                .setPositiveButton(R.string.enquiry_sent_done, (dialog, which) -> finish())
                .setNegativeButton(R.string.enquiry_sent_edit, null)
                .show();
    }

    private void bindViews() {
        nameLayout = findViewById(R.id.name_layout);
        emailLayout = findViewById(R.id.email_layout);
        phoneLayout = findViewById(R.id.phone_layout);
        programmeLayout = findViewById(R.id.programme_layout);
        messageLayout = findViewById(R.id.message_layout);
        nameInput = findViewById(R.id.name_input);
        emailInput = findViewById(R.id.email_input);
        phoneInput = findViewById(R.id.phone_input);
        messageInput = findViewById(R.id.message_input);
        programmeInput = findViewById(R.id.programme_input);

        clearErrorOnEdit(nameInput, nameLayout);
        clearErrorOnEdit(emailInput, emailLayout);
        clearErrorOnEdit(phoneInput, phoneLayout);
        clearErrorOnEdit(messageInput, messageLayout);
    }

    /** Removes a field's error as soon as the prospect starts correcting it. */
    private void clearErrorOnEdit(TextInputEditText input, TextInputLayout layout) {
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable text) {
                layout.setError(null);
            }
        });
    }

    private void setUpProgrammeDropdown() {
        List<String> options = new ArrayList<>();
        options.add(getString(R.string.enquiry_general_option));
        for (Course course : CourseRepository.getInstance(this).getCourses()) {
            options.add(course.getName());
        }
        programmeInput.setAdapter(new ArrayAdapter<>(this, R.layout.item_dropdown_option,
                R.id.option_text, options));
        programmeInput.setText(selectedProgramme, false);
        programmeInput.setOnItemClickListener((parent, view, position, id) -> {
            selectedProgramme = options.get(position);
            programmeLayout.setError(null);
        });
    }

    private void submit() {
        String name = textOf(nameInput);
        String email = textOf(emailInput);
        String phone = textOf(phoneInput);
        String message = textOf(messageInput);

        boolean valid = check(nameLayout, !name.isEmpty(), R.string.enquiry_error_name);
        valid &= check(emailLayout, Patterns.EMAIL_ADDRESS.matcher(email).matches(),
                R.string.enquiry_error_email);
        valid &= check(phoneLayout, PHONE_PATTERN.matcher(phone).matches(),
                R.string.enquiry_error_phone);
        valid &= check(programmeLayout, !TextUtils.isEmpty(selectedProgramme),
                R.string.enquiry_error_programme);
        valid &= check(messageLayout, !message.isEmpty(), R.string.enquiry_error_message);
        if (!valid) {
            return;
        }

        handedOff = ExternalLinks.composeEmail(
                this,
                getString(R.string.email_enquiry),
                getString(R.string.enquiry_email_subject, selectedProgramme),
                getString(R.string.enquiry_email_body, name, email, phone, selectedProgramme,
                        message));
    }

    /** Shows or clears a field's error message, and returns whether the field passed. */
    private boolean check(TextInputLayout layout, boolean passed, @StringRes int errorRes) {
        layout.setError(passed ? null : getString(errorRes));
        return passed;
    }

    private static String textOf(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString().trim();
    }
}
