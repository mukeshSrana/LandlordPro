package com.landlordpro.dto.constants;

public class Patterns {
    public static final String NAME_PATTERN_LETTER_AND_SPACES = "^[a-zA-ZÆØÅæøåé\\s]+$"; //used for personname, ownername, country. expensename, expenseloaction
    public static final String CITY_PATTERN_LETTER_AND_SPACES = "^(?!City not found$)(?!Error fetching city$)[a-zA-ZÆØÅæøåé\\s]+$";
    public static final String ADDRESS1_PATTERN_LETTER_AND_SPACES = "^[a-zA-ZÆØÅæøåé0-9\\s.,#-]+$";
    public static final String ADDRESS2_PATTERN_LETTER_AND_SPACES = "^[a-zA-ZÆØÅæøåé0-9\\s.,#-]*$";
    public static final String PINCODE_PATTERN_FOUR_DIGITS = "^[0-9]{4}$";
    public static final String APARTMENT_NAME_PATTERN_LETTER_WITHOUT_SPACES = "^[a-zA-Z0-9]+$";
    public static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    public static final String MOBILE_NR_PATTERN = "^\\+?[0-9]{8,12}$";
    public static final String LOCALDATE_PATTERN = "^\\d{2}-\\d{2}-\\d{4}$";
}
