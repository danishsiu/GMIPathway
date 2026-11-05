package com.week14.gmipathway.data;

import com.week14.gmipathway.R;
import com.week14.gmipathway.model.CampusPhoto;
import com.week14.gmipathway.model.Facility;
import com.week14.gmipathway.model.TechCentre;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * GMI's campus facilities and technology centres.
 * Source: research/campus_research.md. Photos are GMI's own, from its Facilities Gallery
 * and technology centre pages, and are credited on screen.
 */
public final class CampusCatalog {

    private static final String CENTRE_URL = "https://www.gmi.edu.my/technology-center/";

    private static final List<Facility> FACILITIES = Collections.unmodifiableList(Arrays.asList(
            new Facility(R.string.facility_mechanical, R.string.facility_mechanical_summary,
                    new CampusPhoto(R.drawable.fac_mech_cnc, R.string.photo_cnc_workshop),
                    new CampusPhoto(R.drawable.fac_mech_dmg, R.string.photo_dmg_workshop),
                    new CampusPhoto(R.drawable.fac_mech_machine_shop, R.string.photo_machine_shop),
                    new CampusPhoto(R.drawable.fac_mech_stamping, R.string.photo_stamping),
                    new CampusPhoto(R.drawable.fac_mech_zeiss, R.string.photo_zeiss),
                    new CampusPhoto(R.drawable.fac_mech_3d_printing, R.string.photo_3d_printers)),
            new Facility(R.string.facility_electrical, R.string.facility_electrical_summary,
                    new CampusPhoto(R.drawable.fac_elec_autotronics, R.string.photo_autotronics),
                    new CampusPhoto(R.drawable.fac_elec_dcs_emerson, R.string.photo_dcs_emerson),
                    new CampusPhoto(R.drawable.fac_elec_dcs_siemens, R.string.photo_dcs_siemens),
                    new CampusPhoto(R.drawable.fac_elec_digital, R.string.photo_digital_lab)),
            new Facility(R.string.facility_computer, R.string.facility_computer_summary,
                    new CampusPhoto(R.drawable.fac_comp_computer_lab, R.string.photo_computer_lab),
                    new CampusPhoto(R.drawable.fac_comp_network, R.string.photo_network_lab),
                    new CampusPhoto(R.drawable.fac_comp_cyber, R.string.photo_cyber_lab),
                    new CampusPhoto(R.drawable.fac_comp_multimedia, R.string.photo_multimedia_lab),
                    new CampusPhoto(R.drawable.fac_comp_post_production,
                            R.string.photo_post_production),
                    new CampusPhoto(R.drawable.fac_comp_cinematic, R.string.photo_cinematic)),
            new Facility(R.string.facility_pre_university, R.string.facility_pre_university_summary,
                    new CampusPhoto(R.drawable.fac_preu_chemistry, R.string.photo_chemistry_lab),
                    new CampusPhoto(R.drawable.fac_preu_lecture_hall, R.string.photo_lecture_hall),
                    new CampusPhoto(R.drawable.fac_preu_classroom, R.string.photo_classroom)),
            new Facility(R.string.facility_library, R.string.facility_library_summary,
                    new CampusPhoto(R.drawable.fac_library_1, R.string.photo_library),
                    new CampusPhoto(R.drawable.fac_library_2, R.string.photo_library),
                    new CampusPhoto(R.drawable.fac_library_3, R.string.photo_library),
                    new CampusPhoto(R.drawable.fac_library_4, R.string.photo_library)),
            new Facility(R.string.facility_sport, R.string.facility_sport_summary,
                    new CampusPhoto(R.drawable.fac_sport_gym, R.string.photo_gym),
                    new CampusPhoto(R.drawable.fac_sport_badminton, R.string.photo_badminton),
                    new CampusPhoto(R.drawable.fac_sport_futsal, R.string.photo_futsal),
                    new CampusPhoto(R.drawable.fac_sport_tennis, R.string.photo_tennis),
                    new CampusPhoto(R.drawable.fac_sport_field, R.string.photo_football_field),
                    new CampusPhoto(R.drawable.fac_sport_netball, R.string.photo_netball)),
            new Facility(R.string.facility_hostel, R.string.facility_hostel_summary,
                    new CampusPhoto(R.drawable.fac_hostel_block, R.string.photo_hostel_block),
                    new CampusPhoto(R.drawable.fac_hostel_room_1, R.string.photo_hostel_room),
                    new CampusPhoto(R.drawable.fac_hostel_room_2, R.string.photo_hostel_room)),
            new Facility(R.string.facility_surau, R.string.facility_surau_summary,
                    new CampusPhoto(R.drawable.fac_surau_outside, R.string.photo_surau_outside),
                    new CampusPhoto(R.drawable.fac_surau_hall, R.string.photo_surau_hall),
                    new CampusPhoto(R.drawable.fac_surau_inside, R.string.photo_surau_inside))));

    private static final List<TechCentre> TECH_CENTRES = Collections.unmodifiableList(Arrays.asList(
            new TechCentre(R.string.centre_precision_machining,
                    R.string.centre_precision_machining_body, R.drawable.tc_precision_machining,
                    null, CENTRE_URL + "precision-machiningtechnology-center/"),
            new TechCentre(R.string.centre_metrology, R.string.centre_metrology_body,
                    R.drawable.tc_metrology, "ZEISS",
                    CENTRE_URL + "industrial-metrology-technologycentre/"),
            new TechCentre(R.string.centre_robotics, R.string.centre_robotics_body,
                    R.drawable.tc_robotics, null,
                    CENTRE_URL + "industrial-robotics-technologycenter/"),
            new TechCentre(R.string.centre_digital_manufacturing,
                    R.string.centre_digital_manufacturing_body, R.drawable.tc_digital_manufacturing,
                    null, CENTRE_URL + "digital-manufacturing-center/"),
            new TechCentre(R.string.centre_sheet_metal, R.string.centre_sheet_metal_body,
                    R.drawable.tc_sheet_metal, "LVD",
                    CENTRE_URL + "sheet-metal-processing-technologycentre-lvd/"),
            new TechCentre(R.string.centre_injection_moulding,
                    R.string.centre_injection_moulding_body, R.drawable.tc_injection_moulding,
                    null, CENTRE_URL + "injectionmoulding-technologycenter/"),
            new TechCentre(R.string.centre_hydraulic, R.string.centre_hydraulic_body,
                    TechCentre.NO_PHOTO, null, CENTRE_URL + "hydraulic-technology-center/"),
            new TechCentre(R.string.centre_dcs_siemens, R.string.centre_dcs_siemens_body,
                    R.drawable.fac_elec_dcs_siemens, "Siemens",
                    CENTRE_URL + "distribution-control-systemtechnology-centersiemens/"),
            new TechCentre(R.string.centre_dcs_emerson, R.string.centre_dcs_emerson_body,
                    R.drawable.tc_dcs_emerson, "Emerson",
                    CENTRE_URL + "distribution-control-system-technology-center-emerson/"),
            new TechCentre(R.string.centre_calibration, R.string.centre_calibration_body,
                    R.drawable.tc_calibration, null,
                    CENTRE_URL + "calibration-management-and-services-centre/"),
            new TechCentre(R.string.centre_cisco, R.string.centre_cisco_body,
                    R.drawable.tc_cisco, "Cisco", CENTRE_URL + "networking-academycisco/"),
            new TechCentre(R.string.centre_cyber_security, R.string.centre_cyber_security_body,
                    R.drawable.fac_comp_cyber, null,
                    CENTRE_URL + "cybersecurity-technology-center/")));

    private static final List<TechCentre> EXAM_CENTRES = Collections.unmodifiableList(Arrays.asList(
            new TechCentre(R.string.exam_cambridge, R.string.exam_cambridge_body,
                    TechCentre.NO_PHOTO, null, CENTRE_URL + "examcenter-cambridge-a-level/"),
            new TechCentre(R.string.exam_telc, R.string.exam_telc_body,
                    TechCentre.NO_PHOTO, null, CENTRE_URL + "examcenter-telc/"),
            new TechCentre(R.string.exam_ielts, R.string.exam_ielts_body,
                    TechCentre.NO_PHOTO, null, "https://www.gmi.edu.my/ielts-exam/")));

    private CampusCatalog() {
    }

    public static List<Facility> getFacilities() {
        return FACILITIES;
    }

    public static List<TechCentre> getTechCentres() {
        return TECH_CENTRES;
    }

    public static List<TechCentre> getExamCentres() {
        return EXAM_CENTRES;
    }
}
