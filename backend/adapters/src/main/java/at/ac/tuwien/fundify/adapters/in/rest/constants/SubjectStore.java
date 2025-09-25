package at.ac.tuwien.fundify.adapters.in.rest.constants;

import at.ac.tuwien.fundify.adapters.in.rest.dto.StandardizedSubjectWebModel;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SubjectStore {

    private static final Map<String, StandardizedSubjectWebModel> subjectsMap;

    private SubjectStore() {}

    // currently stored in memory, but could be loaded from a database or external service
    //
    // this is the OEFOS 3-digit (second level) code to subject mapping
    // The Austrian Fields of Science and Technology Classification (OEFOS) is the Austrian version of the revised
    // international Fields of Science and Technology Classification of the OECD (FOS)
    static {
        Map<String, StandardizedSubjectWebModel> map = new HashMap<>();
        map.put("1", new StandardizedSubjectWebModel(1, "1", "NATURAL SCIENCES"));
        map.put("101", new StandardizedSubjectWebModel(2, "101", "Mathematics"));
        map.put("102", new StandardizedSubjectWebModel(2, "102", "Computer Sciences"));
        map.put("103", new StandardizedSubjectWebModel(2, "103", "Physics, Astronomy"));
        map.put("104", new StandardizedSubjectWebModel(2, "104", "Chemistry"));
        map.put("105", new StandardizedSubjectWebModel(2, "105", "Geosciences"));
        map.put("106", new StandardizedSubjectWebModel(2, "106", "Biology"));
        map.put("107", new StandardizedSubjectWebModel(2, "107", "Other Natural Sciences"));
        map.put("2", new StandardizedSubjectWebModel(1, "2", "TECHNICAL SCIENCES"));
        map.put("201", new StandardizedSubjectWebModel(2, "201", "Construction Engineering"));
        map.put("202", new StandardizedSubjectWebModel(2, "202", "Electrical Engineering, Electronics, Information Engineering"));
        map.put("203", new StandardizedSubjectWebModel(2, "203", "Mechanical Engineering"));
        map.put("204", new StandardizedSubjectWebModel(2, "204", "Chemical Process Engineering"));
        map.put("205", new StandardizedSubjectWebModel(2, "205", "Materials Engineering"));
        map.put("206", new StandardizedSubjectWebModel(2, "206", "Medical Engineering"));
        map.put("207", new StandardizedSubjectWebModel(2, "207", "Environmental Engineering, Applied Geosciences"));
        map.put("208", new StandardizedSubjectWebModel(2, "208", "Environmental Biotechnology"));
        map.put("209", new StandardizedSubjectWebModel(2, "209", "Industrial Biotechnology"));
        map.put("210", new StandardizedSubjectWebModel(2, "210", "Nanotechnology"));
        map.put("211", new StandardizedSubjectWebModel(2, "211", "Other Technical Sciences"));
        map.put("3", new StandardizedSubjectWebModel(1, "3", "HUMAN MEDICINE, HEALTH SCIENCES"));
        map.put("301", new StandardizedSubjectWebModel(2, "301", "Medical-Theoretical Sciences, Pharmacy"));
        map.put("302", new StandardizedSubjectWebModel(2, "302", "Clinical Medicine"));
        map.put("303", new StandardizedSubjectWebModel(2, "303", "Health Sciences"));
        map.put("304", new StandardizedSubjectWebModel(2, "304", "Medical Biotechnology"));
        map.put("305", new StandardizedSubjectWebModel(2, "305", "Other Human Medicine, Health Sciences"));
        map.put("4", new StandardizedSubjectWebModel(1, "4", "AGRICULTURAL SCIENCES, VETERINARY MEDICINE"));
        map.put("401", new StandardizedSubjectWebModel(2, "401", "Agriculture and Forestry, Fishery"));
        map.put("402", new StandardizedSubjectWebModel(2, "402", "Animal Breeding, Animal Production"));
        map.put("403", new StandardizedSubjectWebModel(2, "403", "Veterinary Medicine"));
        map.put("404", new StandardizedSubjectWebModel(2, "404", "Agricultural Biotechnology, Food Biotechnology"));
        map.put("405", new StandardizedSubjectWebModel(2, "405", "Other Agricultural Sciences"));
        map.put("5", new StandardizedSubjectWebModel(1, "5", "SOCIAL SCIENCES"));
        map.put("501", new StandardizedSubjectWebModel(2, "501", "Psychology"));
        map.put("502", new StandardizedSubjectWebModel(2, "502", "Economics"));
        map.put("503", new StandardizedSubjectWebModel(2, "503", "Educational Sciences"));
        map.put("504", new StandardizedSubjectWebModel(2, "504", "Sociology"));
        map.put("505", new StandardizedSubjectWebModel(2, "505", "Law"));
        map.put("506", new StandardizedSubjectWebModel(2, "506", "Political Science"));
        map.put("507", new StandardizedSubjectWebModel(2, "507", "Human Geography, Regional Geography, Regional Planning"));
        map.put("508", new StandardizedSubjectWebModel(2, "508", "Media and Communication Sciences"));
        map.put("509", new StandardizedSubjectWebModel(2, "509", "Other Social Sciences"));
        map.put("6", new StandardizedSubjectWebModel(1, "6", "HUMANITIES"));
        map.put("601", new StandardizedSubjectWebModel(2, "601", "History, Archaeology"));
        map.put("602", new StandardizedSubjectWebModel(2, "602", "Linguistics and Literature"));
        map.put("603", new StandardizedSubjectWebModel(2, "603", "Philosophy, Ethics, Religion"));
        map.put("604", new StandardizedSubjectWebModel(2, "604", "Arts"));
        map.put("605", new StandardizedSubjectWebModel(2, "605", "Other Humanities"));
        subjectsMap = map;
    }


    public static Collection<StandardizedSubjectWebModel> getAllSubjects() {
        return subjectsMap.values();
    }

    public static StandardizedSubjectWebModel getSubjectByCode(String key) {
        return subjectsMap.get(key);
    }
    
    
}