'use client';

import { useEffect, useState } from 'react';
import Image from 'next/image';
import { apiCall } from '@/lib/api';
import { useCart } from '@/context/CartContext';

interface Product {
  id: string;
  name: string;
  description: string;
  price: number;
  category: string;
  images: string[];
  stock: number;
}

export default function ShopPage() {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    async function fetchProducts() {
      try {
        const response = await apiCall('/products/active');
        setProducts(response.data);
      } catch (err: any) {
        setError('Failed to load products. Please try again later.');
      } finally {
        setLoading(false);
      }
    }
    fetchProducts();
  }, []);

  const { addItem } = useCart();

  if (loading) return (
    <div className="min-h-screen flex items-center justify-center">
      <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-primary"></div>
    </div>
  );

  return (
    <div className="min-h-screen bg-slate-50 pt-24 pb-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-7xl mx-auto">
        <header className="mb-12 text-center">
          <h1 className="text-4xl font-extrabold tracking-tight text-slate-900 sm:text-5xl">
            The <span className="premium-gradient-text">YAMATEE</span> Collection
          </h1>
          <p className="mt-4 text-xl text-secondary max-w-2xl mx-auto italic">
            "Where tech-driven design meets premium street culture."
          </p>
        </header>

        {error && <div className="text-center text-red-500 mb-8">{error}</div>}

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-8">
          {products.map((product) => (
            <div key={product.id} className="glass-card group flex flex-col overflow-hidden hover:shadow-2xl transition-all duration-300">
              <div className="relative aspect-[4/5] bg-slate-200 overflow-hidden">
                <img
                  src={product.images?.[0] || 'https://images.unsplash.com/photo-1551794840-8ae3b9c081f7?q=80&w=1887&auto=format&fit=crop'}
                  alt={product.name}
                  className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110"
                />
                <div className="absolute inset-0 bg-black/5 group-hover:bg-black/20 transition-colors" />
                <button 
                  onClick={() => addItem({
                    id: product.id,
                    name: product.name,
                    price: product.price,
                    quantity: 1,
                    image: product.images?.[0]
                  })}
                  className="absolute bottom-4 left-4 right-4 translate-y-12 opacity-0 group-hover:translate-y-0 group-hover:opacity-100 btn-primary py-2 text-sm transition-all shadow-xl"
                >
                  Quick Add to Cart
                </button>
              </div>

              <div className="p-6 flex-grow flex flex-col">
                <div className="flex justify-between items-start mb-2">
                  <h3 className="font-bold text-lg text-slate-900 group-hover:text-primary transition-colors">
                    {product.name}
                  </h3>
                  <span className="font-mono font-bold text-primary">
                    ${product.price}
                  </span>
                </div>
                <p className="text-secondary text-sm line-clamp-2 mb-4">
                  {product.description}
                </p>
                <div className="mt-auto flex items-center justify-between text-xs text-secondary font-medium uppercase tracking-widest">
                  <span>{product.category}</span>
                  <span className={product.stock > 0 ? "text-green-500" : "text-red-500"}>
                    {product.stock > 0 ? "In Stock" : "Sold Out"}
                  </span>
                </div>
              </div>
            </div>
          ))}
        </div>

        {products.length === 0 && !error && (
          <div className="text-center py-24 glass-card">
            <h3 className="text-xl font-medium text-secondary">No products found. Stay tuned for the next drop!</h3>
          </div>
        )}
      </div>
    </div>
  );
}
