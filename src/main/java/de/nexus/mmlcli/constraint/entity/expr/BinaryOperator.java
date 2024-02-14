package de.nexus.mmlcli.constraint.entity.expr;

import hipe.pattern.ComparatorType;

public enum BinaryOperator {
    EQUALS(ComparatorType.EQUAL) {
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
    NOT_EQUALS(ComparatorType.UNEQUAL) {
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
    GREATER_THAN(ComparatorType.GREATER) {
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
    LESS_THAN(ComparatorType.LESS) {
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
    GREATER_EQUAL_THAN(ComparatorType.GREATER_OR_EQUAL) {
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
    LESS_EQUAL_THAN(ComparatorType.LESS_OR_EQUAL) {
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
    LOGICAL_AND {
        @Override
        public boolean applyBool(boolean a, boolean b) {
            return a && b;
        }
    },
    LOGICAL_OR {
        @Override
        public boolean applyBool(boolean a, boolean b) {
            return a || b;
        }
    },
    ADDITION {
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
    SUBTRACTION {
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
    MULTIPLICATION {
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
    DIVISION {
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
    EXPONENTIATION {
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
    MODULO {
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

    BinaryOperator(ComparatorType hipeType) {
        this.hipeType = hipeType;
    }

    BinaryOperator() {
        this.hipeType = null;
    }

    public ComparatorType asHiPEComparatorType() {
        return this.hipeType;
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
