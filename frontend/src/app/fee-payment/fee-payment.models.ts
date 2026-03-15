export interface FeePaymentRequest {
  studentNumber: string;
  paymentAmount: number;
  paymentDate?: string;
}

export interface FeePaymentResponse {
  studentNumber: string;
  previousBalance: number;
  paymentAmount: number;
  incentiveRate: number;
  incentiveAmount: number;
  newBalance: number;
  nextPaymentDueDate: string;
}

export type ApiValidationError = Record<string, string>;

export interface ApiErrorResponse {
  message?: string;
  status?: number;
  path?: string;
  timestamp?: string;
}

