import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HotelService } from '../../shared/services/hotel.service';

@Component({
  selector: 'app-hotel-form',
  templateUrl: './hotel-form.component.html',
  styleUrls: ['./hotel-form.component.scss']
})
export class HotelFormComponent implements OnInit {
  form!: FormGroup;
  isEdit = false;
  hotelId?: number;
  loading = false;
  error = '';

  constructor(
    private fb: FormBuilder,
    private hotelService: HotelService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      name:        ['', [Validators.required, Validators.maxLength(255)]],
      address:     ['', Validators.required],
      city:        ['', Validators.required],
      country:     ['', Validators.required],
      starRating:  [null, [Validators.min(1), Validators.max(5)]],
      phone:       [''],
      email:       ['', Validators.email],
      description: ['', Validators.maxLength(1000)]
    });

    this.hotelId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.hotelId) {
      this.isEdit = true;
      this.hotelService.getById(this.hotelId).subscribe({
        next: h => this.form.patchValue(h),
        error: () => this.error = 'Failed to load hotel'
      });
    }
  }

  get f() { return this.form.controls; }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.loading = true;
    this.error = '';
    const data = this.form.value;
    const req = this.isEdit
      ? this.hotelService.update(this.hotelId!, data)
      : this.hotelService.create(data);

    req.subscribe({
      next: () => this.router.navigate(['/hotels']),
      error: err => {
        this.error = err.error?.message || 'Save failed';
        this.loading = false;
      }
    });
  }
}
