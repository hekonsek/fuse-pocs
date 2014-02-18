package fuse.pocs.blueprint.beanvalidator.event;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.List;

public class NoDuplicatesValidator implements ConstraintValidator<NoDuplicates, List<String>> {

    @Override
    public void initialize(NoDuplicates noDuplicates) {
    }

    @Override
    public boolean isValid(List<String> strings, ConstraintValidatorContext constraintValidatorContext) {
        return new HashSet<String>(strings).size() == strings.size();
    }

}