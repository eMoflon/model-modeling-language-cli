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

    /**
     * Get the wrapper value as string
     *
     * @return the value as String
     */
    public String getAsString() {
        return String.valueOf(this.value);
    }

    /**
     * Get the wrapped value as int
     *
     * @return the value as primitive int
     * @throws UnsupportedOperationException if the value is not an integer
     */
    public int getAsInt() {
        if (this.type == ValueWrapperType.INTEGER) {
            return (int) this.value;
        } else {
            throw new UnsupportedOperationException("Unable to convert " + this.type + " to int");
        }
    }

    /**
     * Get the wrapped value as double
     *
     * @return the value as primitive double
     * @throws UnsupportedOperationException if the value is not a double
     */
    public double getAsDouble() {
        if (this.type == ValueWrapperType.INTEGER || this.type == ValueWrapperType.DOUBLE || this.type == ValueWrapperType.FLOAT) {
            return Double.parseDouble(this.value.toString());
        } else {
            throw new UnsupportedOperationException("Unable to convert " + this.type + " to double");
        }
    }

    /**
     * Get the wrapped value as float
     *
     * @return the value as primitive float
     * @throws UnsupportedOperationException if the value is not a float
     */
    public float getAsFloat() {
        if (this.type == ValueWrapperType.FLOAT) {
            return (float) this.value;
        } else {
            throw new UnsupportedOperationException("Unable to convert " + this.type + " to float");
        }
    }

    /**
     * Get the wrapped value as boolean
     *
     * @return the value as primitive boolean
     * @throws UnsupportedOperationException if the value is not a boolean
     */
    public boolean getAsBoolean() {
        if (this.type == ValueWrapperType.BOOLEAN) {
            return (boolean) this.value;
        } else {
            throw new UnsupportedOperationException("Unable to convert " + this.type + " to boolean");
        }
    }

    /**
     * Get the wrapped value as Enumerator
     *
     * @return the value as Enumerator
     * @throws UnsupportedOperationException if the value is not an Enum Enumerator
     */
    public Enumerator getAsEnumEnumerator() {
        if (this.type == ValueWrapperType.EENUM_ENUMERATOR) {
            return (Enumerator) this.value;
        } else {
            throw new UnsupportedOperationException("Unable to convert " + this.type + " to enum enumerator");
        }
    }

    /**
     * Check whether the wrapped value is a string
     *
     * @return whether the value is a string
     */
    public boolean isString() {
        return this.type == ValueWrapperType.STRING;
    }

    /**
     * Check whether the wrapped value is an integer
     *
     * @return whether the value is an integer
     */
    public boolean isInteger() {
        return this.type == ValueWrapperType.INTEGER;
    }

    /**
     * Check whether the wrapped value is a double
     *
     * @return whether the value is a double
     */
    public boolean isDouble() {
        return this.type == ValueWrapperType.DOUBLE;
    }

    /**
     * Check whether the wrapped value is a float
     *
     * @return whether the value is a float
     */
    public boolean isFloat() {
        return this.type == ValueWrapperType.FLOAT;
    }

    /**
     * Check whether the wrapped value is a boolean
     *
     * @return whether the value is a boolean
     */
    public boolean isBoolean() {
        return this.type == ValueWrapperType.BOOLEAN;
    }

    /**
     * Check whether the wrapped value is an Enumerator
     *
     * @return whether the value is an Enumerator
     */
    public boolean isEEnumEnumerator() {
        return this.type == ValueWrapperType.EENUM_ENUMERATOR;
    }

    /**
     * Equality
     * <p>
     * this == other
     *
     * @param other other ValueWrapper
     * @return the wrapped result of the equality check
     */
    public ValueWrapper<?> equals(ValueWrapper<?> other) {
        return ValueWrapper.create(this.value.equals(other.value));
    }

    /**
     * Inequality
     * <p>
     * this != other
     *
     * @param other other ValueWrapper
     * @return the wrapped result of the inequality check
     */
    public ValueWrapper<?> notEquals(ValueWrapper<?> other) {
        return ValueWrapper.create(!this.value.equals(other.value));
    }

    /**
     * Relational Greater
     * <p>
     * this > other
     *
     * @param other other ValueWrapper
     * @return the wrapped result of the relational greater
     * @throws UnsupportedOperationException if the operation is not supported by the operand types
     */
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

    /**
     * Relational less
     * <p>
     * this < other
     *
     * @param other other ValueWrapper
     * @return the wrapped result of the relational less
     * @throws UnsupportedOperationException if the operation is not supported by the operand types
     */
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

    /**
     * Relational greater equal
     * <p>
     * this >= other
     *
     * @param other other ValueWrapper
     * @return the wrapped result of the relational greater equal
     * @throws UnsupportedOperationException if the operation is not supported by the operand types
     */
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

    /**
     * Relational less equal
     * <p>
     * this <= other
     *
     * @param other other ValueWrapper
     * @return the wrapped result of the relational less equal
     * @throws UnsupportedOperationException if the operation is not supported by the operand types
     */
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

    /**
     * Negation
     * <p>
     * !this
     *
     * @return the wrapped result of the negation
     * @throws UnsupportedOperationException if the operation is not supported by the operand types
     */
    public ValueWrapper<?> neg() {
        return switch (this.type) {
            case BOOLEAN -> ValueWrapper.create(!this.getAsBoolean());
            default -> throw new UnsupportedOperationException("Unable to compare with self of type " + this.type);
        };
    }

    /**
     * Addition
     * <p>
     * this + other
     *
     * @param other other ValueWrapper
     * @return the wrapped result of the addition
     * @throws UnsupportedOperationException if the operation is not supported by the operand types
     */
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

    /**
     * Subtraction
     * <p>
     * this - other
     *
     * @param other other ValueWrapper
     * @return the wrapped result of the subtraction
     * @throws UnsupportedOperationException if the operation is not supported by the operand types
     */
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

    /**
     * Multiplication
     * <p>
     * this * other
     *
     * @param other other ValueWrapper
     * @return the wrapped result of the multiplication
     * @throws UnsupportedOperationException if the operation is not supported by the operand types
     */
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

    /**
     * Division
     * <p>
     * this / other
     *
     * @param other other ValueWrapper
     * @return the wrapped result of the division
     * @throws UnsupportedOperationException if the operation is not supported by the operand types
     */
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

    /**
     * Modulo
     * <p>
     * this % other
     *
     * @param other other ValueWrapper
     * @return the wrapped result of the modulo
     * @throws UnsupportedOperationException if the operation is not supported by the operand types
     */
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

    /**
     * Exponentation
     * <p>
     * this ** other
     *
     * @param other other ValueWrapper
     * @return the wrapped result of the exponentation
     * @throws UnsupportedOperationException if the operation is not supported by the operand types
     */
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

    /**
     * Logical AND
     * <p>
     * this && other
     *
     * @param other other ValueWrapper
     * @return the wrapped result of the logical AND
     * @throws UnsupportedOperationException if the operation is not supported by the operand types
     */
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

    /**
     * Logical OR
     * <p>
     * this || other
     *
     * @param other other ValueWrapper
     * @return the wrapped result of the logical OR
     */
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

    /**
     * Wrap an EMF attribute value
     *
     * @param type  EClassifier as type of the attribute
     * @param value Attribute value
     * @return the wrapped attribute value
     */
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
