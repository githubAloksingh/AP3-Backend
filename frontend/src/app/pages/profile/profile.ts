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
    address: ''
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
    console.log('[ProfileComponent] Loading profile for customerId:', customerId);
    this.customerService.getCustomerById(customerId).subscribe({
      next: (data) => {
        console.log('[ProfileComponent] Profile loaded successfully:', data);
        this.customer = data;
        this.loading.set(false);
      },
      error: (err) => {
        console.error('[ProfileComponent] Profile load error:', err);
        this.loading.set(false);
        this.errorMessage.set(err.error?.message || 'Failed to load customer profile.');
      }
    });
  }

  onSubmit(): void {
    const customerId = this.authService.getCustomerId();
    if (!customerId) return;

    // Validation (phone number exactly 10 digits if provided)
    if (this.customer.phoneNumber && !/^[0-9]{10}$/.test(this.customer.phoneNumber)) {
      this.errorMessage.set('Phone number must be exactly 10 digits.');
      return;
    }

    this.errorMessage.set('');
    this.successMessage.set('');
    this.saving.set(true);

    this.customerService.updateCustomer(customerId, this.customer).subscribe({
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
}
