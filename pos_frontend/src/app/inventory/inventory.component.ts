import { Component, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Inventory } from '../interfaces/inventory.interface';
import { ToastService } from '../services/toast.service';
import { InventoryListComponent } from './components/inventory-list/inventory-list.component';
import { ImportInventoryComponent } from './components/import-inventory/import-inventory.component';

@Component({
  selector: 'app-inventory',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    InventoryListComponent,
    ImportInventoryComponent
  ],
  templateUrl: './inventory.component.html'
})
export class InventoryComponent {
  @ViewChild(InventoryListComponent) private inventoryList!: InventoryListComponent;
  
  inventory: Inventory[] = [];
  currentPage = 1;
  hasNextPage = false;
  loading = false;
  searchTerm = '';
  showImportModal = false;

  constructor(private toastService: ToastService) {}

  onPageChange(page: number) {
    this.currentPage = page;
  }

  onSearchInput(term: string) {
    this.searchTerm = term;
  }

  showImport() {
    this.showImportModal = true;
  }

  cancelImport() {
    this.showImportModal = false;
  }

  saveImport() {
    this.showImportModal = false;
    this.inventoryList.fetchInventory();
  }
} 