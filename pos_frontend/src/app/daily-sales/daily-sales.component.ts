import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DailySalesService } from '../services/daily-sales.service';
import { DailySalesData } from '../interfaces/daily-sales.interface';
import { ToastService } from '../services/toast.service';
import { ProductService } from '../services/product.service';
import { ClientService } from '../services/client.service';
import { Product } from '../interfaces/product.interface';
import { Client } from '../interfaces/client.interface';

interface DailySalesFilters {
  startDate: string | null;
  endDate: string | null;
}

@Component({
  selector: 'app-daily-sales',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './daily-sales.component.html',
  styleUrls: ['./daily-sales.component.scss']
})
export class DailySalesComponent implements OnInit {
  salesData: DailySalesData[] = [];
  loading = false;
  error: string | null = null;
  filters = {
    startDate: null as string | null,
    endDate: null as string | null
  };
  minDate: string = '';
  maxDate: string = '';
  totalOrders = 0;
  totalProducts = 0;
  totalRevenue = 0;
  searchTerm = '';
  searchResults: Product[] = [];
  selectedProduct: Product | null = null;
  selectedClient: Client | null = null;
  clients: Client[] = [];

  constructor(
    private dailySalesService: DailySalesService,
    private productService: ProductService,
    private clientService: ClientService,
    private toastService: ToastService
  ) {}

  ngOnInit() {
    this.fetchDailySales();
    const today = new Date();
    this.maxDate = today.toISOString().split('T')[0];

    // Set min date to 1 year ago
    const lastYear = new Date();
    lastYear.setFullYear(lastYear.getFullYear() - 1);
    this.minDate = lastYear.toISOString().split('T')[0];

    //this.loadClients();
  }

  fetchDailySales() {
    this.loading = true;
    this.error = null;

    const startDate = this.filters.startDate ? new Date(this.filters.startDate) : undefined;
    const endDate = this.filters.endDate ? new Date(this.filters.endDate) : undefined;

    this.dailySalesService.getDailySales(startDate, endDate)
      .subscribe({
        next: (data) => {
          this.salesData = data.sort((a, b) => 
            new Date(b.date).getTime() - new Date(a.date).getTime()
          );
          this.calculateTotals();
          this.loading = false;
        },
        error: (error) => {
          console.error('Error fetching daily sales:', error);
          this.error = 'Failed to fetch daily sales data';
          this.loading = false;
        }
      });
  }

  validateDates() {
    if (this.filters.startDate && this.filters.endDate) {
      const startDate = new Date(this.filters.startDate);
      const endDate = new Date(this.filters.endDate);
      
      if (endDate < startDate) {
        this.toastService.show('End date cannot be before start date', 'error');
        this.filters.endDate = '';
        return false;
      }
    }
    return true;
  }

  applyFilters() {
    if (!this.validateDates()) return;
    
    this.loading = true;
    this.error = null;

    this.fetchDailySales();
  }

  clearFilters() {
    this.filters = {
      startDate: null,
      endDate: null
    };
    this.fetchDailySales();
  }

  private calculateTotals() {
    this.totalOrders = this.salesData.reduce((sum, day) => sum + day.orderCount, 0);
    this.totalProducts = this.salesData.reduce((sum, day) => sum + day.productCount, 0);
    this.totalRevenue = this.salesData.reduce((sum, day) => sum + day.revenue, 0);
  }
} 