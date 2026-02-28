import { Component, OnInit ,EventEmitter, Output, Input, OnChanges, SimpleChanges, ViewChild} from '@angular/core';
import { FormGroupDirective, NgForm } from '@angular/forms';
import { AuthService } from '../_services/auth.service';


@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit, OnChanges {
  form: any = {
    username: null,
    email: null,
    password: null
  };
  isSuccessful = false;
  isSignUpFailed = false;
  errorMessage = '';
  roles: string[] = ['admin'];

  @ViewChild('f') testFormElement!: NgForm;
  @Input() msgFromDad!: boolean;
  @Output() signup = new EventEmitter<boolean>();
  constructor(private authService: AuthService) { }
  ngOnChanges(changes: SimpleChanges): void
  {
    console.log(this.msgFromDad)
     if (this.msgFromDad )
     {
       if (this.testFormElement.invalid) {
         console.log("mpika sto invalid")
         return;


       }
       else {
          console.log("mpika sto invalid")
         this.onSubmit();
       }
    }
    this.msgFromDad = false;
  }

  ngOnInit(): void {
    console.log(this.msgFromDad);
  }

  onSubmit(): void {
    const { username, email, password } = this.form;
    this.authService.register(username, email, password, this.roles).subscribe(
      data => {
        console.log(data);
        this.isSuccessful = true;
        this.isSignUpFailed = false;
        this.signup.emit(this.isSuccessful);
      },
      err => {
        this.errorMessage = err.error.message;
        this.isSignUpFailed = true;
        this.signup.emit(this.isSignUpFailed);
      }
    );
  }
  //for step 1
  get username() { return this.form.get('username'); }
}
