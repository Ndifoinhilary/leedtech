import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { FeePaymentResponse } from './fee-payment.models';
import { FeePaymentService } from './fee-payment.service';

@Component({
  selector: 'app-fee-payment-page',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './fee-payment-page.component.html'
})
export class FeePaymentPageComponent {
  private readonly fb = inject(FormBuilder);
  private readonly feePaymentService = inject(FeePaymentService);

  protected readonly loading = signal(false);
  protected readonly submitError = signal<string | null>(null);
  protected readonly result = signal<FeePaymentResponse | null>(null);

  protected readonly paymentForm = this.fb.nonNullable.group({
    studentNumber: ['', [Validators.required]],
    paymentAmount: [0, [Validators.required, Validators.min(0.01)]],
    paymentDate: ['']
  });

  protected submit(): void {
    if (this.paymentForm.invalid) {
      this.paymentForm.markAllAsTouched();
      return;
    }

    const raw = this.paymentForm.getRawValue();
    this.loading.set(true);
    this.submitError.set(null);
    this.result.set(null);

    const payload = {
      studentNumber: raw.studentNumber.trim(),
      paymentAmount: Number(raw.paymentAmount),
      ...(raw.paymentDate ? { paymentDate: raw.paymentDate } : {})
    };

    this.feePaymentService.processOneTimePayment(payload).subscribe({
      next: (response) => {
        this.result.set(response);
        this.loading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.submitError.set(this.getErrorMessage(error));
        this.loading.set(false);
      }
    });
  }

  protected reset(): void {
    this.paymentForm.reset({
      studentNumber: '',
      paymentAmount: 0,
      paymentDate: ''
    });
    this.submitError.set(null);
    this.result.set(null);
  }

  protected hasError(fieldName: 'studentNumber' | 'paymentAmount', errorCode: string): boolean {
    const control = this.paymentForm.controls[fieldName];
    return !!control && control.touched && control.hasError(errorCode);
  }

  protected formatCurrency(value: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(value);
  }

  protected formatPercentage(value: number): string {
    return `${(value * 100).toFixed(2)}%`;
  }

  private getErrorMessage(error: HttpErrorResponse): string {
    if (error.error && typeof error.error === 'object') {
      if ('message' in error.error && typeof error.error.message === 'string') {
        return error.error.message;
      }

      const fieldMessages = Object.values(error.error).filter((value) => typeof value === 'string') as string[];
      if (fieldMessages.length > 0) {
        return fieldMessages.join(' | ');
      }
    }

    return 'Unable to process payment. Please verify your input and try again.';
  }
}

