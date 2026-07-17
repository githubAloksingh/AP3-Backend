export interface TransferRequest {
  fromAccountNumber: string;
  toAccountNumber?: string;
  receiverPhoneNumber?: string;
  amount: number;
  description?: string;
}

export interface Transaction {
  id: number;
  fromAccountNumber: string;
  toAccountNumber: string;
  senderName: string;
  receiverName: string;
  senderCountry: string;
  receiverCountry: string;
  amount: number;
  transactionType: string;
  status: string;
  description?: string;
  createdAt: string;
}
