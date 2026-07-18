import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { Account } from '../../models/account.model';
import { Customer } from '../../models/customer.model';
import { AccountService } from '../../services/account.service';
import { CustomerService } from '../../services/customer.service';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin.html'
})
export class AdminComponent implements OnInit {
  private readonly customerService = inject(CustomerService);
  private readonly accountService = inject(AccountService);
  customers = signal<Customer[]>([]);
  accounts = signal<Account[]>([]);
  loading = signal<boolean>(true);
  errorMessage = signal<string>('');

  selectedCustomerId = signal<number | null>(null);

  get selectedCustomerAccounts(): Account[] {
    const id = this.selectedCustomerId();
    if (!id) return [];
    return this.accounts().filter((a) => a.customerId === id);
  }

  get selectedCustomerName(): string {
    const id = this.selectedCustomerId();
    if (!id) return '';

    const customer = this.customers().find((item) => item.id === id);
    return customer ? `${customer.firstName} ${customer.lastName}` : '';
  }

  ngOnInit(): void {
    let completed = 0;
    const done = () => {
      completed += 1;
      if (completed === 2) this.loading.set(false);
    };
    this.customerService.getAllCustomers().subscribe({
      next: (data) => {
        this.customers.set(data);
        done();
      },
      error: (err) => {
        this.errorMessage.set(err.error?.message || 'Failed to load customers.');
        done();
      }
    });

    this.accountService.getAllAccounts().subscribe({
      next: (data) => {
        this.accounts.set(data);
        done();
      },
      error: (err) => {
        this.errorMessage.set(err.error?.message || 'Failed to load accounts.');
        done();
      }
    });

    // transactions removed
  }

  selectCustomer(customer: Customer): void {
    if (customer.id == null) return;
    this.selectedCustomerId.set(customer.id);
  }

}
