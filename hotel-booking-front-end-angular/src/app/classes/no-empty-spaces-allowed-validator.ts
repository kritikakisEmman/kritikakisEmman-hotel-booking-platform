import { FormControl, ValidationErrors } from "@angular/forms";

export class NoEmptySpacesAllowedValidator {



  // whitespace validation
  static notWhitespaceAtAll(control: FormControl): ValidationErrors {

    // check if string only contains whitespace
    if ((control.value != null) && (control.value.split(" ").length > 1)) {

      // invalid, return error object
      return { 'notWhitespaceAtAll': true };
    }
    else {
      // valid, return null
      return null as any;
    }
  }
}
