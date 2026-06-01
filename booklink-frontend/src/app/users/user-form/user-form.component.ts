import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { UserService } from '../../shared/services/user.service';

@Component({
  selector: 'app-user-form',
  templateUrl: './user-form.component.html',
  styleUrls: ['./user-form.component.scss']
})
export class UserFormComponent implements OnInit {
  form!: FormGroup;
  isEdit = false;
  userId?: number;
  loading = false;
  error = '';

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      email:    ['', [Validators.required, Validators.email]],
      password: ['', [Validators.minLength(6)]],
      role:     ['USER', Validators.required]
    });

    this.userId = Number(this.route.snapshot.paramMap.get('id')) || undefined;
    if (this.userId) {
      this.isEdit = true;
      this.userService.getById(this.userId).subscribe({
        next: u => this.form.patchValue(u),
        error: () => this.error = 'Failed to load user'
      });
    } else {
      this.form.get('password')?.addValidators(Validators.required);
    }
  }

  get f() { return this.form.controls; }

  submit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.loading = true;
    this.error = '';
    const req = this.isEdit
      ? this.userService.update(this.userId!, this.form.value)
      : this.userService.create(this.form.value);

    req.subscribe({
      next: () => this.router.navigate(['/users']),
      error: err => { this.error = err.error?.message || 'Save failed'; this.loading = false; }
    });
  }
}
