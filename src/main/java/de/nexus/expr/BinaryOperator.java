package de.nexus.expr;

import hipe.pattern.ComparatorType;

public enum BinaryOperator {
    EQUALS(ComparatorType.EQUAL, true) {
        @Override
        public boolean applyBool(int a, int b) {
            return a == b;
        }

        @Override
        public boolean applyBool(double a, double b) {
            return a == b;
        }

        @Override
        public boolean applyBool(int a, double b) {
            return a == b;
        }

        @Override
        public boolean applyBool(double a, int b) {
            return a == b;
        }

        @Override
        public boolean applyBool(boolean a, boolean b) {
            return a == b;
        }

        @Override
        public boolean applyBool(String a, String b) {
            return a.equals(b);
        }
    },
    NOT_EQUALS(ComparatorType.UNEQUAL, true) {
        @Override
        public boolean applyBool(int a, int b) {
            return a != b;
        }

        @Override
        public boolean applyBool(double a, double b) {
            return a != b;
        }

        @Override
        public boolean applyBool(int a, double b) {
            return a != b;
        }

        @Override
        public boolean applyBool(double a, int b) {
            return a != b;
        }

        @Override
        public boolean applyBool(boolean a, boolean b) {
            return a != b;
        }

        @Override
        public boolean applyBool(String a, String b) {
            return !a.equals(b);
        }
    },
    GREATER_THAN(ComparatorType.GREATER, false) {
        @Override
        public boolean applyBool(int a, int b) {
            return a > b;
        }

        @Override
        public boolean applyBool(double a, double b) {
            return a > b;
        }

        @Override
        public boolean applyBool(int a, double b) {
            return a > b;
        }

        @Override
        public boolean applyBool(double a, int b) {
            return a > b;
        }
    },
    LESS_THAN(ComparatorType.LESS, false) {
        @Override
        public boolean applyBool(int a, int b) {
            return a < b;
        }

        @Override
        public boolean applyBool(double a, double b) {
            return a < b;
        }

        @Override
        public boolean applyBool(int a, double b) {
            return a < b;
        }

        @Override
        public boolean applyBool(double a, int b) {
            return a < b;
        }
    },
    GREATER_EQUAL_THAN(ComparatorType.GREATER_OR_EQUAL, false) {
        @Override
        public boolean applyBool(int a, int b) {
            return a >= b;
        }

        @Override
        public boolean applyBool(double a, double b) {
            return a >= b;
        }

        @Override
        public boolean applyBool(int a, double b) {
            return a >= b;
        }

        @Override
        public boolean applyBool(double a, int b) {
            return a >= b;
        }
    },
    LESS_EQUAL_THAN(ComparatorType.LESS_OR_EQUAL, false) {
        @Override
        public boolean applyBool(int a, int b) {
            return a <= b;
        }

        @Override
        public boolean applyBool(double a, double b) {
            return a <= b;
        }

        @Override
        public boolean applyBool(int a, double b) {
            return a <= b;
        }

        @Override
        public boolean applyBool(double a, int b) {
            return a <= b;
        }
    },
    LOGICAL_AND(true) {
        @Override
        public boolean applyBool(boolean a, boolean b) {
            return a && b;
        }
    },
    LOGICAL_OR(true) {
        @Override
        public boolean applyBool(boolean a, boolean b) {
            return a || b;
        }
    },
    ADDITION(false) {
        @Override
        public String applyString(String a, String b) {
            return a + b;
        }

        @Override
        public double applyDouble(int a, int b) {
            return a + b;
        }

        @Override
        public double applyDouble(double a, double b) {
            return a + b;
        }

        @Override
        public double applyDouble(int a, double b) {
            return a + b;
        }

        @Override
        public double applyDouble(double a, int b) {
            return a + b;
        }
    },
    SUBTRACTION(false) {
        @Override
        public double applyDouble(int a, int b) {
            return a - b;
        }

        @Override
        public double applyDouble(double a, double b) {
            return a - b;
        }

        @Override
        public double applyDouble(int a, double b) {
            return a - b;
        }

        @Override
        public double applyDouble(double a, int b) {
            return a - b;
        }
    },
    MULTIPLICATION(false) {
        @Override
        public double applyDouble(int a, int b) {
            return a * b;
        }

        @Override
        public double applyDouble(double a, double b) {
            return a * b;
        }

        @Override
        public double applyDouble(int a, double b) {
            return a * b;
        }

        @Override
        public double applyDouble(double a, int b) {
            return a * b;
        }

        @Override
        public String applyString(int a, String b) {
            return b.repeat(a);
        }

        @Override
        public String applyString(String a, int b) {
            return a.repeat(b);
        }
    },
    DIVISION(false) {
        @Override
        public double applyDouble(int a, int b) {
            return (double) a / b;
        }

        @Override
        public double applyDouble(double a, double b) {
            return a / b;
        }

        @Override
        public double applyDouble(int a, double b) {
            return a / b;
        }

        @Override
        public double applyDouble(double a, int b) {
            return a / b;
        }
    },
    EXPONENTIATION(false) {
        @Override
        public double applyDouble(int a, int b) {
            return Math.pow(a, b);
        }

        @Override
        public double applyDouble(double a, double b) {
            return Math.pow(a, b);
        }

        @Override
        public double applyDouble(int a, double b) {
            return Math.pow(a, b);
        }

        @Override
        public double applyDouble(double a, int b) {
            return Math.pow(a, b);
        }
    },
    MODULO(false) {
        @Override
        public double applyDouble(int a, int b) {
            return a % b;
        }

        @Override
        public double applyDouble(double a, double b) {
            return a % b;
        }

        @Override
        public double applyDouble(int a, double b) {
            return a % b;
        }

        @Override
        public double applyDouble(double a, int b) {
            return a % b;
        }
    },
    ;

    private final ComparatorType hipeType;
    private final boolean isBooleanOperator;

    BinaryOperator(ComparatorType hipeType, boolean isBooleanOperator) {
        this.hipeType = hipeType;
        this.isBooleanOperator = isBooleanOperator;
    }

    BinaryOperator(boolean isBooleanOperator) {
        this.isBooleanOperator = isBooleanOperator;
        this.hipeType = null;
    }

    public ComparatorType asHiPEComparatorType() {
        return this.hipeType;
    }

    public boolean isBooleanOperator() {
        return isBooleanOperator;
    }

    public boolean isHiPEComparator() {
        return this.hipeType != null;
    }

    public boolean applyBool(int a, int b) {
        throw new UnsupportedOperationException("");
    }

    public boolean applyBool(double a, double b) {
        throw new UnsupportedOperationException("");
    }

    public boolean applyBool(int a, double b) {
        throw new UnsupportedOperationException("");
    }

    public boolean applyBool(double a, int b) {
        throw new UnsupportedOperationException("");
    }

    public boolean applyBool(boolean a, boolean b) {
        throw new UnsupportedOperationException("");
    }

    public boolean applyBool(String a, String b) {
        throw new UnsupportedOperationException("");
    }

    public String applyString(int a, int b) {
        throw new UnsupportedOperationException("");
    }

    public String applyString(double a, double b) {
        throw new UnsupportedOperationException("");
    }

    public String applyString(int a, double b) {
        throw new UnsupportedOperationException("");
    }

    public String applyString(double a, int b) {
        throw new UnsupportedOperationException("");
    }

    public String applyString(boolean a, boolean b) {
        throw new UnsupportedOperationException("");
    }

    public String applyString(int a, String b) {
        throw new UnsupportedOperationException("");
    }

    public String applyString(String a, int b) {
        throw new UnsupportedOperationException("");
    }

    public String applyString(String a, String b) {
        throw new UnsupportedOperationException("");
    }

    public double applyDouble(int a, int b) {
        throw new UnsupportedOperationException("");
    }

    public double applyDouble(double a, double b) {
        throw new UnsupportedOperationException("");
    }

    public double applyDouble(int a, double b) {
        throw new UnsupportedOperationException("");
    }

    public double applyDouble(double a, int b) {
        throw new UnsupportedOperationException("");
    }

    public double applyDouble(boolean a, boolean b) {
        throw new UnsupportedOperationException("");
    }

    public double applyDouble(String a, String b) {
        throw new UnsupportedOperationException("");
    }
}
