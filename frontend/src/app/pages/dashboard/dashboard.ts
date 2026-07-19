import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { AccountService } from '../../services/account.service';
import { Account } from '../../models/account.model';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html'
})
export class DashboardComponent implements OnInit {
  protected readonly authService = inject(AuthService);
  private readonly accountService = inject(AccountService);

  accounts = signal<Account[]>([]);
  totalBalance = signal<number>(0);
  showBalances = signal<boolean>(false);
  loading = signal<boolean>(true);
  errorMessage = signal<string>('');

  ngOnInit(): void {
    const customerId = this.authService.getCustomerId();
    if (customerId) {
      this.loadAccounts(customerId);
    } else {
      this.loading.set(false);
      this.errorMessage.set('Customer session could not be established.');
    }
  }

  loadAccounts(customerId: number): void {
    console.log('[DashboardComponent] Loading accounts for customerId:', customerId);
    this.accountService.getAccountsByCustomerId(customerId).subscribe({
      next: (data) => {
        console.log('[DashboardComponent] Accounts loaded successfully:', data);
        this.accounts.set(data);
        this.totalBalance.set(data.reduce((sum, acc) => sum + acc.balance, 0));
        this.loading.set(false);
      },
      error: (err) => {
        console.error('[DashboardComponent] Accounts load error:', err);
        this.loading.set(false);
        this.errorMessage.set(err.error?.message || 'Failed to load accounts. Please refresh the page.');
      }
    });
  }

  protected maskAccountNumber(accountNumber?: string): string {
    if (!accountNumber) return '••••';
    const visibleDigits = 4;
    return `${'•'.repeat(Math.max(0, accountNumber.length - visibleDigits))}${accountNumber.slice(-visibleDigits)}`;
  }

  protected toggleBalances(): void {
    this.showBalances.update(value => !value);
  }
}
