package com.landlordpro.dto.constants;

public enum DeductibleExpense {

    MEGLER_ANNONSERING_VISNINGER("Kostnader ved bruk av megler, annonsering, visninger"),
    DEPOSITUMSKONTO("Depositumskonto"),
    FORSIKRING("Forsikring"),
    KOMMUNALE_AVGIFTER("Kommunale avgifter"),
    EIENDOMSSKATT("Eiendomsskatt"),
    FESTEAVGIFT("Festeavgift"),
    FELLESKOSTNADER("Felleskostnader i sameier og borettslag"),
    STROM_OG_OPPVARMING("Strøm og oppvarming"),
    REISEUTGIFTER("Reiseutgifter"),
    VEDLIKEHOLDSKOSTNADER("Vedlikeholdskostnader"),
    EGET_ARBEID("Eget arbeid"),
    MOBLER_OG_INNBO("Møbler og innbo"),
    ANDRE_KOSTNADER("Andre kostnader");

    private final String description;

    DeductibleExpense(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}

