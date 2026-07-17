import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './reset-password.html'
})
export class ResetPasswordComponent {
  private readonly fb = inject(FormBuilder);
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  submitting = signal(false);
  message = signal('');
  errorMessage = signal('');

  resetForm = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    currentPassword: ['', Validators.required],
    newPassword: ['', [Validators.required, Validators.minLength(4)]]
  });

  submit(): void {
    this.message.set('');
    this.errorMessage.set('');
    this.resetForm.markAllAsTouched();

    if (this.resetForm.invalid) {
      return;
    }

    this.submitting.set(true);
    this.http.post('http://localhost:8081/auth/reset-password', this.resetForm.getRawValue()).subscribe({
      next: () => {
        this.submitting.set(false);
        this.message.set('Password updated successfully. You can now log in.');
        setTimeout(() => this.router.navigate(['/login']), 1200);
      },
      error: (err) => {
        this.submitting.set(false);
        this.errorMessage.set(err.error?.message || 'Failed to reset password.');
      }
    });
  }
}
