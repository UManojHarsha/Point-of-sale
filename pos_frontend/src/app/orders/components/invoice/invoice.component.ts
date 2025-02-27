import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Order } from '../../../interfaces/order.interface';
import { OrderService } from '../../../services/order.service';
import { ToastService } from '../../../services/toast.service';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-invoice',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './invoice.component.html'
})
export class InvoiceComponent {
  @Input() selectedOrder: Order | null = null;
  @Output() close = new EventEmitter<void>();
  
  pdfSrc: SafeResourceUrl | null = null;

  constructor(
    private orderService: OrderService,
    private toastService: ToastService,
    private sanitizer: DomSanitizer
  ) {}

  loadPdf() {
    if (!this.selectedOrder) return;

    this.orderService.getOrderPdf(this.selectedOrder.id).subscribe({
      next: (base64Pdf) => {
        try {
          const binaryString = window.atob(base64Pdf.trim());
          const bytes = new Uint8Array(binaryString.length);
          for (let i = 0; i < binaryString.length; i++) {
            bytes[i] = binaryString.charCodeAt(i);
          }
          
          const blob = new Blob([bytes], { type: 'application/pdf' });
          const url = window.URL.createObjectURL(blob);
          this.pdfSrc = this.sanitizer.bypassSecurityTrustResourceUrl(url);
        } catch (e) {
          console.error('Error processing PDF:', e);
          this.toastService.show('Error processing PDF data', 'error');
        }
      },
      error: (error) => {
        console.error('Error fetching PDF:', error);
        this.toastService.show('Failed to load invoice PDF', 'error');
      }
    });
  }

  downloadPdf() {
    if (!this.selectedOrder) return;

    this.orderService.downloadInvoice(this.selectedOrder.id).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `invoice-${this.selectedOrder?.id}.pdf`;
        link.click();
        window.URL.revokeObjectURL(url);
        this.toastService.show('Invoice downloaded successfully', 'success');
      },
      error: (error) => {
        console.error('Error downloading invoice:', error);
        this.toastService.show('Failed to download invoice', 'error');
      }
    });
  }

  onClose() {
    this.close.emit();
  }
}
