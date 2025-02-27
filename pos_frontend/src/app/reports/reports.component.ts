import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReportService } from '../services/report.service';
import { ReportForm, ReportData } from '../interfaces/report.interface';
import { OnInit } from '@angular/core';
import { ToastService } from '../services/toast.service';
import { ProductService } from '../services/product.service';
import { ClientService } from '../services/client.service';
import { Product } from '../interfaces/product.interface';
import { Client } from '../interfaces/client.interface';
import { Subject, debounceTime, distinctUntilChanged } from 'rxjs';

interface AggregatedReportData {
  product_name: string;
  client_name: string;
  barcode: string;
  total_quantity: number;
  total_price: number;
}

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.scss']
})
export class ReportsComponent implements OnInit {
  filters: ReportForm = {};
  reportData: AggregatedReportData[] = [];
  loading = false;
  error: string | null = null;
  totalQuantity = 0;
  totalAmount = 0;
  minDate: string = '';
  maxDate: string = '';
  searchTerm = '';
  searchResults: Product[] = [];
  selectedProduct: Product | null = null;
  clients: Client[] = [];
  selectedClient: Client | null = null;
  private searchSubject = new Subject<string>();

  constructor(
    private reportService: ReportService,
    private productService: ProductService,
    private clientService: ClientService,
    private toastService: ToastService
  ) {
    // Setup search debounce
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(term => {
      if (term.length >= 1) {
        this.productService.searchProducts(term).subscribe({
          next: (response) => {
            this.searchResults = response.data;
          },
          error: (error) => {
            console.error('Error searching products:', error);
            this.toastService.show('Failed to search products', 'error');
          }
        });
      } else {
        this.searchResults = [];
      }
    });
  }

  ngOnInit() {
    // Set max date to today
    const today = new Date();
    this.maxDate = today.toISOString().split('T')[0];

    // Set min date to 1 year ago
    const lastYear = new Date();
    lastYear.setFullYear(lastYear.getFullYear() - 1);
    this.minDate = lastYear.toISOString().split('T')[0];
    this.loadClients();
  }

  private loadClients() {
    this.clientService.getClients(1, 1000).subscribe({
      next: (response) => {
        this.clients = response.data;
      },
      error: (error) => {
        console.error('Error loading clients:', error);
        this.toastService.show('Failed to load clients', 'error');
      }
    });
  }

  onProductSearch(term: string) {
    this.searchSubject.next(term);
  }

  onProductSelect(product: Product) {
    this.selectedProduct = product;
    this.searchResults = [];
    this.searchTerm = '';
  }

  validateDates() {
    if (this.filters.fromDate && this.filters.toDate) {
      const startDate = new Date(this.filters.fromDate);
      const endDate = new Date(this.filters.toDate);
      
      if (endDate < startDate) {
        this.toastService.show('End date cannot be before start date', 'error');
        this.filters.toDate = '';
        this.filters.fromDate = '';
        return false;
      }
    }
    return true;
  }

  applyFilters() {
    if (!this.validateDates()) return;
    
    this.loading = true;
    this.error = null;

    this.reportService.getReport(this.filters).subscribe({
      next: (data) => {
        this.aggregateReportData(data);
        this.calculateTotals();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error fetching report:', error);
        this.error = 'Failed to fetch report data';
        this.loading = false;
      }
    });
  }

  private aggregateReportData(data: ReportData[]) {
    // Create a map to store aggregated data
    const aggregatedMap = new Map<string, AggregatedReportData>();

    // Aggregate data by product
    data.forEach(item => {
      const key = `${item.productName}-${item.barcode}`;
      if (aggregatedMap.has(key)) {
        const existing = aggregatedMap.get(key)!;
        existing.total_quantity += item.quantity;
        existing.total_price += item.price;
      } else {
        aggregatedMap.set(key, {
          product_name: item.productName,
          client_name: item.clientName,
          barcode: item.barcode,
          total_quantity: item.quantity,
          total_price: item.price
        });
      }
    });

    // Convert map to array and sort by product name
    this.reportData = Array.from(aggregatedMap.values())
      .sort((a, b) => a.product_name.localeCompare(b.product_name));
  }

  clearFilters() {
    this.filters = {};
    this.reportData = [];
    this.totalQuantity = 0;
    this.totalAmount = 0;
  }

  private calculateTotals() {
    this.totalQuantity = this.reportData.reduce((sum, item) => sum + item.total_quantity, 0);
    this.totalAmount = this.reportData.reduce((sum, item) => sum + item.total_price, 0);
  }
} 