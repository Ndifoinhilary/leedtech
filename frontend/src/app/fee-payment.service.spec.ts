import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { FeePaymentService } from './fee-payment.service';

describe('FeePaymentService', () => {
  let service: FeePaymentService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(FeePaymentService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('posts one-time payment payload to backend endpoint', () => {
    const payload = {
      studentNumber: 'STD-1001',
      paymentAmount: 100000,
      paymentDate: '2026-03-14'
    };

    service.processOneTimePayment(payload).subscribe();

    const req = httpMock.expectOne('http://localhost:8080/one-time-fee-payment');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(payload);
    req.flush({});
  });
});

