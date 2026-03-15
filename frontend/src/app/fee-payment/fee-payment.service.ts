import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { FeePaymentRequest, FeePaymentResponse } from './fee-payment.models';

@Injectable({ providedIn: 'root' })
export class FeePaymentService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/one-time-fee-payment';

  processOneTimePayment(payload: FeePaymentRequest): Observable<FeePaymentResponse> {
    return this.http.post<FeePaymentResponse>(this.baseUrl, payload);
  }
}

