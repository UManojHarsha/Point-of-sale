export interface ReportForm {
  productName?: string;
  clientName?: string;
  barcode?: string;
  fromDate?: string | null;
  toDate?: string | null;
}

export interface ReportData {
  productName: string;
  clientName: string;
  barcode: string;
  quantity: number;
  price: number;
} 