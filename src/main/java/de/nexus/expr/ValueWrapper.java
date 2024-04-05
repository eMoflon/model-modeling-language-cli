package de.nexus.expr;

import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EcorePackage;

import java.util.Objects;

public class ValueWrapper<T> {
    private final ValueWrapperType type;
    private final T value;

    public ValueWrapper(ValueWrapperType type, T value) {
        this.type = type;
        this.value = value;
    }

    public String getAsString() {
        return String.valueOf(this.value);
    }

    public int getAsInt() {
        if (this.type == ValueWrapperType.INTEGER) {
            return (int) this.value;
        } else {
            throw new UnsupportedOperationException("Unable to convert " + this.type + " to int");
        }
    }

    public double getAsDouble() {
        if (this.type == ValueWrapperType.INTEGER || this.type == ValueWrapperType.DOUBLE || this.type == ValueWrapperType.FLOAT) {
            return Double.parseDouble(this.value.toString());
        } else {
            throw new UnsupportedOperationException("Unable to convert " + this.type + " to double");
        }
    }

    public float getAsFloat() {
        if (this.type == ValueWrapperType.FLOAT) {
            return (float) this.value;
        } else {
            throw new UnsupportedOperationException("Unable to convert " + this.type + " to float");
        }
    }

    public boolean getAsBoolean() {
        if (this.type == ValueWrapperType.BOOLEAN) {
            return (boolean) this.value;
        } else {
            throw new UnsupportedOperationException("Unable to convert " + this.type + " to boolean");
        }
    }

    public Enumerator getAsEnumEnumerator() {
        if (this.type == ValueWrapperType.EENUM_ENUMERATOR) {
            return (Enumerator) this.value;
        } else {
            throw new UnsupportedOperationException("Unable to convert " + this.type + " to enum enumerator");
        }
    }

    public boolean isString() {
        return this.type == ValueWrapperType.STRING;
    }

    public boolean isInteger() {
        return this.type == ValueWrapperType.INTEGER;
    }

    public boolean isDouble() {
        return this.type == ValueWrapperType.DOUBLE;
    }

    public boolean isFloat() {
        return this.type == ValueWrapperType.FLOAT;
    }

    public boolean isBoolean() {
        return this.type == ValueWrapperType.BOOLEAN;
    }

    public boolean isEEnumEnumerator() {
        return this.type == ValueWrapperType.EENUM_ENUMERATOR;
    }


    public ValueWrapper<?> equals(ValueWrapper<?> other) {
        return ValueWrapper.create(this.value.equals(other.value));
    }

    public ValueWrapper<?> notEquals(ValueWrapper<?> other) {
        return ValueWrapper.create(!this.value.equals(other.value));
    }

    public ValueWrapper<?> greater(ValueWrapper<?> other) {
        return switch (this.type) {
            case INTEGER, DOUBLE, FLOAT -> switch (other.type) {
                case INTEGER, DOUBLE, FLOAT -> ValueWrapper.create(this.getAsDouble() > other.getAsDouble());
                default ->
                        throw new UnsupportedOperationException("Unable to compare with other of type " + other.type);
            };
            default -> throw new UnsupportedOperationException("Unable to compare with self of type " + this.type);
        };
    }

    public ValueWrapper<?> less(ValueWrapper<?> other) {
        return switch (this.type) {
            case INTEGER, DOUBLE, FLOAT -> switch (other.type) {
                case INTEGER, DOUBLE, FLOAT -> ValueWrapper.create(this.getAsDouble() < other.getAsDouble());
                default ->
                        throw new UnsupportedOperationException("Unable to compare with other of type " + other.type);
            };
            default -> throw new UnsupportedOperationException("Unable to compare with self of type " + this.type);
        };
    }

    public ValueWrapper<?> greaterEq(ValueWrapper<?> other) {
        return switch (this.type) {
            case INTEGER, DOUBLE, FLOAT -> switch (other.type) {
                case INTEGER, DOUBLE, FLOAT -> ValueWrapper.create(this.getAsDouble() >= other.getAsDouble());
                default ->
                        throw new UnsupportedOperationException("Unable to compare with other of type " + other.type);
            };
            default -> throw new UnsupportedOperationException("Unable to compare with self of type " + this.type);
        };
    }

    public ValueWrapper<?> lessEq(ValueWrapper<?> other) {
        return switch (this.type) {
            case INTEGER, DOUBLE, FLOAT -> switch (other.type) {
                case INTEGER, DOUBLE, FLOAT -> ValueWrapper.create(this.getAsDouble() <= other.getAsDouble());
                default ->
                        throw new UnsupportedOperationException("Unable to compare with other of type " + other.type);
            };
            default -> throw new UnsupportedOperationException("Unable to compare with self of type " + this.type);
        };
    }

    public ValueWrapper<?> neg() {
        return switch (this.type) {
            case BOOLEAN -> ValueWrapper.create(!this.getAsBoolean());
            default -> throw new UnsupportedOperationException("Unable to compare with self of type " + this.type);
        };
    }

    public ValueWrapper<?> add(ValueWrapper<?> other) {
        return switch (this.type) {
            case INTEGER -> switch (other.type) {
                case INTEGER -> ValueWrapper.create(this.getAsInt() + other.getAsInt());
                case DOUBLE, FLOAT -> ValueWrapper.create(this.getAsDouble() + other.getAsDouble());
                case STRING -> ValueWrapper.create(this.getAsString() + other.getAsString());
                default -> throw new UnsupportedOperationException("Unable to add with other of type " + other.type);
            };
            case DOUBLE -> switch (other.type) {
                case INTEGER, DOUBLE, FLOAT -> ValueWrapper.create(this.getAsDouble() + other.getAsDouble());
                case STRING -> ValueWrapper.create(this.getAsString() + other.getAsString());
                default -> throw new UnsupportedOperationException("Unable to add with other of type " + other.type);
            };
            case FLOAT -> switch (other.type) {
                case INTEGER, DOUBLE -> ValueWrapper.create(this.getAsDouble() + other.getAsDouble());
                case FLOAT -> ValueWrapper.create(this.getAsFloat() + other.getAsFloat());
                case STRING -> ValueWrapper.create(this.getAsString() + other.getAsString());
                default -> throw new UnsupportedOperationException("Unable to add with other of type " + other.type);
            };
            case STRING -> ValueWrapper.create(this.getAsString() + other.getAsString());
            case BOOLEAN -> switch (other.type) {
                case STRING -> ValueWrapper.create(this.getAsString() + other.getAsString());
                default -> throw new UnsupportedOperationException("Unable to add with other of type " + other.type);
            };
            default -> throw new UnsupportedOperationException("Unable to add with this of type " + this.type);
        };
    }

    public ValueWrapper<?> sub(ValueWrapper<?> other) {
        return switch (this.type) {
            case INTEGER -> switch (other.type) {
                case INTEGER -> ValueWrapper.create(this.getAsInt() - other.getAsInt());
                case DOUBLE, FLOAT -> ValueWrapper.create(this.getAsDouble() - other.getAsDouble());
                default ->
                        throw new UnsupportedOperationException("Unable to subtract with other of type " + other.type);
            };
            case DOUBLE -> switch (other.type) {
                case INTEGER, DOUBLE, FLOAT -> ValueWrapper.create(this.getAsDouble() - other.getAsDouble());
                default ->
                        throw new UnsupportedOperationException("Unable to subtract with other of type " + other.type);
            };
            case FLOAT -> switch (other.type) {
                case INTEGER, DOUBLE -> ValueWrapper.create(this.getAsDouble() - other.getAsDouble());
                case FLOAT -> ValueWrapper.create(this.getAsFloat() - other.getAsFloat());
                default ->
                        throw new UnsupportedOperationException("Unable to subtract with other of type " + other.type);
            };
            default -> throw new UnsupportedOperationException("Unable to subtract with self of type " + this.type);
        };
    }

    public ValueWrapper<?> mult(ValueWrapper<?> other) {
        return switch (this.type) {
            case INTEGER -> switch (other.type) {
                case INTEGER -> ValueWrapper.create(this.getAsInt() * other.getAsInt());
                case DOUBLE, FLOAT -> ValueWrapper.create(this.getAsDouble() * other.getAsDouble());
                case STRING -> ValueWrapper.create(other.getAsString().repeat(this.getAsInt()));
                default ->
                        throw new UnsupportedOperationException("Unable to multiply with other of type " + other.type);
            };
            case DOUBLE -> switch (other.type) {
                case INTEGER, DOUBLE, FLOAT -> ValueWrapper.create(this.getAsDouble() * other.getAsDouble());
                default ->
                        throw new UnsupportedOperationException("Unable to multiply with other of type " + other.type);
            };
            case FLOAT -> switch (other.type) {
                case INTEGER, DOUBLE -> ValueWrapper.create(this.getAsDouble() * other.getAsDouble());
                case FLOAT -> ValueWrapper.create(this.getAsFloat() * other.getAsFloat());
                default ->
                        throw new UnsupportedOperationException("Unable to multiply with other of type " + other.type);
            };
            case STRING -> switch (other.type) {
                case INTEGER -> ValueWrapper.create(this.getAsString().repeat(other.getAsInt()));
                default ->
                        throw new UnsupportedOperationException("Unable to multiply with other of type " + other.type);
            };
            default -> throw new UnsupportedOperationException("Unable to multiply with self of type " + this.type);
        };
    }

    public ValueWrapper<?> div(ValueWrapper<?> other) {
        return switch (this.type) {
            case INTEGER -> switch (other.type) {
                case INTEGER -> ValueWrapper.create(this.getAsInt() / other.getAsInt());
                case DOUBLE, FLOAT -> ValueWrapper.create(this.getAsDouble() / other.getAsDouble());
                default -> throw new UnsupportedOperationException("Unable to divide with other of type " + other.type);
            };
            case DOUBLE -> switch (other.type) {
                case INTEGER, DOUBLE, FLOAT -> ValueWrapper.create(this.getAsDouble() / other.getAsDouble());
                default -> throw new UnsupportedOperationException("Unable to divide with other of type " + other.type);
            };
            case FLOAT -> switch (other.type) {
                case INTEGER, DOUBLE -> ValueWrapper.create(this.getAsDouble() / other.getAsDouble());
                case FLOAT -> ValueWrapper.create(this.getAsFloat() / other.getAsFloat());
                default -> throw new UnsupportedOperationException("Unable to divide with other of type " + other.type);
            };
            default -> throw new UnsupportedOperationException("Unable to divide with self of type " + this.type);
        };
    }

    public ValueWrapper<?> mod(ValueWrapper<?> other) {
        return switch (this.type) {
            case INTEGER -> switch (other.type) {
                case INTEGER -> ValueWrapper.create(this.getAsInt() % other.getAsInt());
                case DOUBLE, FLOAT -> ValueWrapper.create(this.getAsDouble() % other.getAsDouble());
                default -> throw new UnsupportedOperationException("Unable to mod with other of type " + other.type);
            };
            case DOUBLE -> switch (other.type) {
                case INTEGER, DOUBLE, FLOAT -> ValueWrapper.create(this.getAsDouble() % other.getAsDouble());
                default -> throw new UnsupportedOperationException("Unable to mod with other of type " + other.type);
            };
            case FLOAT -> switch (other.type) {
                case INTEGER, DOUBLE -> ValueWrapper.create(this.getAsDouble() % other.getAsDouble());
                case FLOAT -> ValueWrapper.create(this.getAsFloat() % other.getAsFloat());
                default -> throw new UnsupportedOperationException("Unable to mod with other of type " + other.type);
            };
            default -> throw new UnsupportedOperationException("Unable to mod with self of type " + this.type);
        };
    }

    public ValueWrapper<?> pow(ValueWrapper<?> other) {
        return switch (this.type) {
            case INTEGER -> switch (other.type) {
                case INTEGER -> ValueWrapper.create(Math.pow(this.getAsInt(), other.getAsInt()));
                case DOUBLE, FLOAT -> ValueWrapper.create(Math.pow(this.getAsDouble(), other.getAsDouble()));
                default -> throw new UnsupportedOperationException("Unable to pow with other of type " + other.type);
            };
            case DOUBLE -> switch (other.type) {
                case INTEGER, DOUBLE, FLOAT -> ValueWrapper.create(Math.pow(this.getAsDouble(), other.getAsDouble()));
                default -> throw new UnsupportedOperationException("Unable to pow with other of type " + other.type);
            };
            case FLOAT -> switch (other.type) {
                case INTEGER, DOUBLE -> ValueWrapper.create(Math.pow(this.getAsDouble(), other.getAsDouble()));
                case FLOAT -> ValueWrapper.create(Math.pow(this.getAsFloat(), other.getAsFloat()));
                default -> throw new UnsupportedOperationException("Unable to pow with other of type " + other.type);
            };
            default -> throw new UnsupportedOperationException("Unable to pow with self of type " + this.type);
        };
    }

    public ValueWrapper<?> and(ValueWrapper<?> other) {
        return switch (this.type) {
            case BOOLEAN -> switch (other.type) {
                case BOOLEAN -> ValueWrapper.create(this.getAsBoolean() && other.getAsBoolean());
                default ->
                        throw new UnsupportedOperationException("Unable to use logical and with other of type " + other.type);
            };
            default ->
                    throw new UnsupportedOperationException("Unable to use logical and with self of type " + this.type);
        };
    }

    public ValueWrapper<?> or(ValueWrapper<?> other) {
        return switch (this.type) {
            case BOOLEAN -> switch (other.type) {
                case BOOLEAN -> ValueWrapper.create(this.getAsBoolean() || other.getAsBoolean());
                default ->
                        throw new UnsupportedOperationException("Unable to use logical or with other of type " + other.type);
            };
            default ->
                    throw new UnsupportedOperationException("Unable to use logical or with self of type " + this.type);
        };
    }

    public static <T> ValueWrapper<?> fromEcoreValue(EClassifier type, T value) {
        if (type instanceof EEnum eEnum) {
            return new ValueWrapper<>(ValueWrapperType.EENUM_ENUMERATOR, eEnum.getEEnumLiteral(value.toString()).getInstance());
        }

        return switch (type.getClassifierID()) {
            case EcorePackage.ESTRING -> ValueWrapper.create(value.toString());
            case EcorePackage.EFLOAT -> ValueWrapper.create(Float.parseFloat(value.toString()));
            case EcorePackage.EDOUBLE -> ValueWrapper.create(Double.parseDouble(value.toString()));
            case EcorePackage.EINT -> ValueWrapper.create(Double.valueOf(value.toString()).intValue());
            case EcorePackage.EBOOLEAN -> ValueWrapper.create(Boolean.parseBoolean(value.toString()));
            default -> new ValueWrapper<>(ValueWrapperType.STRING, value.toString());
        };
    }

    public static ValueWrapper<String> create(String value) {
        return new ValueWrapper<>(ValueWrapperType.STRING, value);
    }

    public static ValueWrapper<Integer> create(int value) {
        return new ValueWrapper<>(ValueWrapperType.INTEGER, value);
    }

    public static ValueWrapper<Double> create(double value) {
        return new ValueWrapper<>(ValueWrapperType.DOUBLE, value);
    }

    public static ValueWrapper<Float> create(float value) {
        return new ValueWrapper<>(ValueWrapperType.FLOAT, value);
    }

    public static ValueWrapper<Boolean> create(boolean value) {
        return new ValueWrapper<>(ValueWrapperType.BOOLEAN, value);
    }

    public static ValueWrapper<Enumerator> create(Enumerator value) {
        return new ValueWrapper<>(ValueWrapperType.EENUM_ENUMERATOR, value);
    }

    @Override
    public String toString() {
        return "ValueWrapper{" +
                "type=" + type +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueWrapper<?> that = (ValueWrapper<?>) o;
        return type == that.type && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }
}
