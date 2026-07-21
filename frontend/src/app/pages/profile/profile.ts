import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { CustomerService } from '../../services/customer.service';
import { Customer } from '../../models/customer.model';

@Component({
  selector: 'app-profile',
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.html'
})
export class ProfileComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly customerService = inject(CustomerService);

  customer: Customer = {
    firstName: '',
    lastName: '',
    email: '',
    phoneNumber: '',
    address: '',
    country: 'INDIA'
  };

  loading = signal<boolean>(true);
  saving = signal<boolean>(false);
  errorMessage = signal<string>('');
  successMessage = signal<string>('');

  ngOnInit(): void {
    const customerId = this.authService.getCustomerId();
    if (customerId) {
      this.loadProfile(customerId);
    } else {
      this.loading.set(false);
      this.errorMessage.set('Session not found.');
    }
  }

  loadProfile(customerId: number): void {
    this.customerService.getCustomerById(customerId).subscribe({
      next: (data) => {
        this.customer = { ...data, country: data.country || 'INDIA' };
        this.loading.set(false);
      },
      error: (err) => {
        this.loading.set(false);
        this.errorMessage.set(err.error?.message || 'Failed to load customer profile.');
      }
    });
  }

  get phonePrefix(): string {
    const country = this.customer.country?.trim().toUpperCase();
    if (country === 'USA' || country === 'US' || country === 'CANADA') {
      return '+1';
    }
    if (country === 'UK') {
      return '+44';
    }
    return '+91'; // Default +91 for India and others
  }

  onPhoneInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    let value = input.value.replace(/[^0-9]/g, '');
    if (value.length > 10) {
      value = value.substring(0, 10);
    }
    input.value = value;
    this.customer.phoneNumber = value;
  }

  onSubmit(): void {
    const customerId = this.authService.getCustomerId();
    if (!customerId) return;

    if (!this.customer.firstName?.trim() || !this.customer.lastName?.trim()) {
      this.errorMessage.set('First name and last name are required.');
      return;
    }

    if (this.customer.phoneNumber && !/^[0-9]{10}$/.test(this.customer.phoneNumber)) {
      this.errorMessage.set('Phone number must be exactly 10 digits.');
      return;
    }

    if (!this.customer.country?.trim()) {
      this.errorMessage.set('Country is required for region validation.');
      return;
    }

    this.errorMessage.set('');
    this.successMessage.set('');
    this.saving.set(true);

    this.customerService.updateCustomer(customerId, {
      ...this.customer,
      country: this.customer.country.trim().toUpperCase()
    }).subscribe({
      next: (data) => {
        this.customer = data;
        this.saving.set(false);
        this.successMessage.set('Profile updated successfully!');
      },
      error: (err) => {
        this.saving.set(false);
        this.errorMessage.set(err.error?.message || 'Failed to update profile.');
      }
    });

  }
  states = [
  { name: 'Delhi', pincode: '110001' },
  { name: 'Rajasthan', pincode: '302001' },
  { name: 'Maharashtra', pincode: '400001' },
  { name: 'Haryana', pincode: '122001' },
  { name: 'Punjab', pincode: '141001' }
];

onStateChange() {

  const selected = this.states.find(
      s => s.name === this.customer.state
  );

  if (selected) {
      this.customer.pincode = selected.pincode;
  }

}
}
