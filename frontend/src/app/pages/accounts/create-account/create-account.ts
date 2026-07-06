import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../services/auth.service';
import { AccountService } from '../../../services/account.service';

@Component({
  selector: 'app-create-account',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './create-account.html'
})
export class CreateAccountComponent {
  private readonly authService = inject(AuthService);
  private readonly accountService = inject(AccountService);
  private readonly router = inject(Router);

  accountType: 'SAVINGS' | 'CURRENT' = 'SAVINGS';
  initialBalance = 0;
  errorMessage = '';
  loading = false;

  onSubmit(): void {
    const customerId = this.authService.getCustomerId();
    if (!customerId) {
      this.errorMessage = 'Active session not found. Please log in.';
      return;
    }

    if (this.initialBalance < 0) {
      this.errorMessage = 'Initial balance cannot be negative.';
      return;
    }

    this.errorMessage = '';
    this.loading = true;

    this.accountService.createAccount({
      accountType: this.accountType,
      balance: this.initialBalance,
      customerId: customerId
    }).subscribe({
      next: () => {
        this.router.navigate(['/accounts']);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error?.message || 'Failed to create account. Please try again.';
      }
    });
  }
}
