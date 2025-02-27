export interface Inventory {
  id: number;
  productId: number;
  totalQuantity: number;
  updatedAt: string;
}

export interface InventoryResponse {
  data: Inventory[];
  total: number;
  hasNextPage: boolean;
} 