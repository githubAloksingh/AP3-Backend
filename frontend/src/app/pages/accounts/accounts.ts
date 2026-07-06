import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { AccountService } from '../../services/account.service';
import { Account } from '../../models/account.model';

@Component({
  selector: 'app-accounts-list',
  imports: [CommonModule, RouterLink],
  templateUrl: './accounts.html'
})
export class AccountsListComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly accountService = inject(AccountService);

  accounts = signal<Account[]>([]);
  loading = signal<boolean>(true);
  errorMessage = signal<string>('');

  ngOnInit(): void {
    const customerId = this.authService.getCustomerId();
    if (customerId) {
      this.loadAccounts(customerId);
    } else {
      this.loading.set(false);
      this.errorMessage.set('Session not found.');
    }
  }

  loadAccounts(customerId: number): void {
    console.log('[AccountsListComponent] Loading accounts for customerId:', customerId);
    this.accountService.getAccountsByCustomerId(customerId).subscribe({
      next: (data) => {
        console.log('[AccountsListComponent] Accounts loaded successfully:', data);
        this.accounts.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('[AccountsListComponent] Accounts load error:', err);
        this.loading.set(false);
        this.errorMessage.set(err.error?.message || 'Failed to load accounts.');
      }
    });
  }
}
