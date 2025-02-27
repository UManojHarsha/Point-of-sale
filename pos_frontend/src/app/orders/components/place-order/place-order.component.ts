import { Component, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { OrderForm } from '../../../interfaces/order.interface';
import { OrderService } from '../../../services/order.service';
import { ProductService } from '../../../services/product.service';
import { ToastService } from '../../../services/toast.service';
import { AuthService } from '../../../services/auth.service';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { Product } from '../../../interfaces/product.interface';
import { OrderItemsData } from '../../../interfaces/order.interface';

interface OrderItem {
  productId: number;
  quantity: number;
  price: number;
}

interface CurrentItem {
  product: Product | null;
  quantity: number;
  price: number;
}

@Component({
  selector: 'app-place-order',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './place-order.component.html'
})
export class PlaceOrderComponent {
  @Output() save = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();

  orderItems: OrderItem[] = [];
  currentItem: CurrentItem = {
    product: null,
    quantity: 1,
    price: 0
  };
  searchTerm = '';
  searchedProducts: Product[] = [];
  private searchSubject = new Subject<string>();
  private productMap = new Map<number, string>();

  constructor(
    private orderService: OrderService,
    private productService: ProductService,
    private toastService: ToastService,
    private authService: AuthService
  ) {
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(term => {
      this.searchProducts(term);
    });
  }

  onSearch(term: string) {
    this.searchSubject.next(term);
  }

  private searchProducts(term: string) {
    if (term.trim()) {
      this.productService.searchProducts(term).subscribe({
        next: (response) => {
          this.searchedProducts = response.data;
        },
        error: (error) => {
          console.error('Error searching products:', error);
          this.toastService.show('Failed to search products', 'error');
        }
      });
    } else {
      this.searchedProducts = [];
    }
  }

  selectProduct(product: Product) {
    this.currentItem.product = product;
    this.currentItem.quantity = 1;
    this.currentItem.price = product.price;
    this.searchTerm = product.name;
    this.productMap.set(product.id, product.name);
    this.searchedProducts = [];
  }

  adjustQuantity(change: number) {
    const newQuantity = this.currentItem.quantity + change;
    if (newQuantity >= 1) {
      this.currentItem.quantity = newQuantity;
      this.updatePrice();
    }
  }

  validateQuantity() {
    if (this.currentItem.quantity < 1) {
      this.currentItem.quantity = 1;
    }
    this.updatePrice();
  }

  private updatePrice() {
    if (this.currentItem.product) {
      this.currentItem.price = this.currentItem.product.price * this.currentItem.quantity;
    }
  }

  addItem() {
    if (this.currentItem.product && this.currentItem.quantity > 0) {
      this.orderItems.push({
        productId: this.currentItem.product.id,
        quantity: this.currentItem.quantity,
        price: this.currentItem.price
      });
      this.currentItem = { product: null, quantity: 1, price: 0 };
      this.searchTerm = '';
      this.toastService.show('Item added to order', 'success');
    }
  }

  removeItem(index: number) {
    this.orderItems.splice(index, 1);
    this.toastService.show('Item removed from order', 'info');
  }

  getProductName(item: OrderItem): string {
    return this.productMap.get(item.productId) || 'Unknown Product';
  }

  getOrderTotal(): number {
    return this.orderItems.reduce((sum, item) => sum + item.price, 0);
  }

  canAddItem(): boolean {
    return !!this.currentItem.product && this.currentItem.product.id > 0 && this.currentItem.quantity > 0;
  }

  canSubmit(): boolean {
    return this.orderItems.length > 0;
  }

  onSubmit() {
    console.log('onSubmit');
    console.log(this.orderItems);
    console.log(this.getOrderTotal());
    console.log(this.authService.getCurrentUserEmail())
    if (this.canSubmit()) {
      const order = {
        userEmail: this.authService.getCurrentUserEmail(),
        totalPrice: this.getOrderTotal(),
        orderItems: this.orderItems
      };

      this.orderService.createOrder(order).subscribe({
        next: () => {
          this.toastService.show('Order placed successfully', 'success');
          this.save.emit();
        },
        error: (error) => {
          console.error('Error placing order:', error);
          this.toastService.show('Failed to place order', 'error');
        }
      });
    }
  }

  onCancel() {
    this.cancel.emit();
  }
} 