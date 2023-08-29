package store.onlinebookstore.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String field;
    private String fieldMatch;

    public void initialize(FieldMatch annotation) {
        field = annotation.field();
        fieldMatch = annotation.fieldMatch();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        Object fieldValue = new BeanWrapperImpl(o).getPropertyValue(field);
        Object fieldMatchValue = new BeanWrapperImpl(o).getPropertyValue(fieldMatch);

        return fieldValue == null && fieldMatchValue == null
                || fieldValue != null && fieldValue.equals(fieldMatchValue);
    }
}
