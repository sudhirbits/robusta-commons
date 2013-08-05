package com.robusta.commons.domain;

import static org.apache.commons.lang3.StringUtils.capitalize;

public enum YesNo {
    YES, NO;

    public static YesNo fromBoolean(boolean yesNoValue) {
        return yesNoValue ? YES : NO;
    }

    public boolean toBoolean() {
        return YES.equals(this);
    }

    @Override
    public String toString() {
        return capitalize(name().toLowerCase());
    }

    public YesNo invert() {
        switch (this) {
            case YES: return NO;
            default: return YES;
        }
    }
}
