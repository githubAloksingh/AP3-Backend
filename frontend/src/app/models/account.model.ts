export interface Account {
  id?: number;
  accountNumber?: string;
  accountType: 'SAVINGS' | 'CURRENT';
  balance: number;
  customerId: number;
  customerName?: string;
  deletionRequested?: boolean;
}
