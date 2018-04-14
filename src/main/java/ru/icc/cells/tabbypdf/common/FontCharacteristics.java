package ru.icc.cells.tabbypdf.common;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author aaltaev
 */
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FontCharacteristics {
    private final static int ALL_CAP = 0b000000001;
    private final static int FIXED_PITCH = 0b000000010;
    private final static int FORCE_BOLD = 0b000000100;
    private final static int ITALIC = 0b000001000;
    private final static int NON_SYMBOLIC = 0b000010000;
    private final static int SCRIPT = 0b000100000;
    private final static int SERIF = 0b001000000;
    private final static int SMALL_CAP = 0b010000000;
    private final static int SYMBOLIC = 0b100000000;

    private int    flags;
    private String fontName;
    private String fontFamily;
    private float  size;
    private float  spaceWidth;

    public boolean isAllCap() {
        return (flags & ALL_CAP) == ALL_CAP;
    }

    public boolean isFixedPitch() {
        return (flags & FIXED_PITCH) == FIXED_PITCH;
    }

    public boolean isForceBold() {
        return (flags & FORCE_BOLD) == FORCE_BOLD;
    }

    public boolean isItalic() {
        return (flags & ITALIC) == ITALIC;
    }

    public boolean isNonSymbolic() {
        return (flags & NON_SYMBOLIC) == NON_SYMBOLIC;
    }

    public boolean isScript() {
        return (flags & SCRIPT) == SCRIPT;
    }

    public boolean isSerif() {
        return (flags & SERIF) == SERIF;
    }

    public boolean isSmallCap() {
        return (flags & SMALL_CAP) == SMALL_CAP;
    }

    public boolean isSymbolic() {
        return (flags & SYMBOLIC) == SYMBOLIC;
    }

    public static Builder newBuilder() {
        return new FontCharacteristics().new Builder();
    }

    public FontCharacteristics copy() {
        FontCharacteristics copy = new FontCharacteristics();
        copy.flags = this.flags;
        copy.fontName = this.fontName;
        copy.fontFamily = this.fontFamily;
        copy.size = this.size;
        return copy;
    }

    public class Builder {
        private Builder() {
        }

        public FontCharacteristics build() {
            return FontCharacteristics.this.copy();
        }

        public Builder setFontName(String fontName) {
            FontCharacteristics.this.fontName = fontName;
            return this;
        }

        public Builder setFontFamily(String fontFamily) {
            FontCharacteristics.this.fontFamily = fontFamily;
            return this;
        }

        public Builder setSize(float size) {
            FontCharacteristics.this.size = size;
            return this;
        }

        public Builder setSpaceWidth(float spaceWidth) {
            FontCharacteristics.this.spaceWidth = spaceWidth;
            return this;
        }

        public Builder setAllCap(boolean value) {
            return setFlag(ALL_CAP, value);
        }

        public Builder setFixedPitch(boolean value) {
            return setFlag(FIXED_PITCH, value);
        }

        public Builder setForceBold(boolean value) {
            return setFlag(FORCE_BOLD, value);
        }

        public Builder setItalic(boolean value) {
            return setFlag(ITALIC, value);
        }

        public Builder setNonSymbolic(boolean value) {
            return setFlag(NON_SYMBOLIC, value);
        }

        public Builder setScript(boolean value) {
            return setFlag(SCRIPT, value);
        }

        public Builder setSerif(boolean value) {
            return setFlag(SERIF, value);
        }

        public Builder setSmallCap(boolean value) {
            return setFlag(SMALL_CAP, value);
        }

        public Builder setSymbolic(boolean value) {
            return setFlag(SYMBOLIC, value);
        }

        private Builder setFlag(int flag, boolean value) {
            if (value) {
                FontCharacteristics.this.flags |= flag;
            } else {
                FontCharacteristics.this.flags &= ~flag;
            }
            return this;
        }
    }
}
