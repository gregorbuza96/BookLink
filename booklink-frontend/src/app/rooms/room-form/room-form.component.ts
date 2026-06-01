import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Hotel, Amenity } from '../../shared/models/models';
import { RoomService } from '../../shared/services/room.service';
import { HotelService } from '../../shared/services/hotel.service';
import { AmenityService } from '../../shared/services/amenity.service';

@Component({
  selector: 'app-room-form',
  templateUrl: './room-form.component.html',
  styleUrls: ['./room-form.component.scss']
})
export class RoomFormComponent implements OnInit {
  form!: FormGroup;
  isEdit = false;
  roomId?: number;
  hotels: Hotel[] = [];
  amenities: Amenity[] = [];
  selectedAmenities: number[] = [];
  loading = false;
  error = '';

  roomTypes = ['SINGLE', 'DOUBLE', 'TRIPLE'];
  comfortLevels = ['STANDARD', 'SUPERIOR'];
  statuses = ['AVAILABLE', 'OCCUPIED'];

  constructor(
    private fb: FormBuilder,
    private roomService: RoomService,
    private hotelService: HotelService,
    private amenityService: AmenityService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      roomNumber:    [null, [Validators.required, Validators.min(1)]],
      type:          ['', Validators.required],
      comfort:       ['', Validators.required],
      pricePerNight: [null, [Validators.required, Validators.min(0.01)]],
      capacity:      [null, [Validators.required, Validators.min(1), Validators.max(4)]],
      status:        ['AVAILABLE', Validators.required],
      description:   ['', Validators.maxLength(500)],
      hotelId:       [null]
    });

    this.hotelService.getAll(0, 100).subscribe({ next: p => this.hotels = p.content });
    this.amenityService.getAll().subscribe({ next: a => this.amenities = a });

    this.roomId = Number(this.route.snapshot.paramMap.get('id')) || undefined;
    if (this.roomId) {
      this.isEdit = true;
      this.roomService.getById(this.roomId).subscribe({
        next: r => {
          this.form.patchValue(r);
          this.selectedAmenities = r.amenityIds || [];
        },
        error: () => this.error = 'Failed to load room'
      });
    }
  }

  get f() { return this.form.controls; }

  toggleAmenity(id: number): void {
    const idx = this.selectedAmenities.indexOf(id);
    idx === -1 ? this.selectedAmenities.push(id) : this.selectedAmenities.splice(idx, 1);
  }

  isAmenitySelected(id: number): boolean {
    return this.selectedAmenities.includes(id);
  }

  submit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.loading = true;
    this.error = '';
    const data = { ...this.form.value, amenityIds: this.selectedAmenities };
    const req = this.isEdit
      ? this.roomService.update(this.roomId!, data)
      : this.roomService.create(data);

    req.subscribe({
      next: () => this.router.navigate(['/rooms']),
      error: err => { this.error = err.error?.message || 'Save failed'; this.loading = false; }
    });
  }
}
