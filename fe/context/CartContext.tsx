'use client';

import React, { createContext, useContext, useState, useEffect } from 'react';
import { apiCall } from '@/lib/api';

interface CartItem {
  id: string;
  name: string;
  price: number;
  quantity: number;
  image?: string;
}

interface CartContextType {
  items: CartItem[];
  addItem: (item: CartItem) => void;
  removeItem: (id: string) => void;
  updateQuantity: (id: string, quantity: number) => void;
  clearCart: () => void;
  totalItems: number;
  totalPrice: number;
  syncCart: (userId: string) => Promise<void>;
}

const CartContext = createContext<CartContextType | undefined>(undefined);

export function CartProvider({ children }: { children: React.ReactNode }) {
  const [items, setItems] = useState<CartItem[]>([]);
  const [userId, setUserId] = useState<string | null>(null);

  useEffect(() => {
    const session = localStorage.getItem('user_session');
    if (session) {
      const user = JSON.parse(session);
      setUserId(user.id);
      syncCart(user.id);
    } else {
      const savedCart = localStorage.getItem('cart');
      if (savedCart) setItems(JSON.parse(savedCart));
    }
  }, []);

  const syncCart = async (uid: string) => {
    try {
      const response = await apiCall(`/cart/${uid}`);
      if (response.data && response.data.items) {
        const mappedItems = response.data.items.map((i: any) => ({
          id: i.productId,
          name: i.productName,
          price: i.price,
          quantity: i.quantity,
          image: i.imageUrl
        }));
        setItems(mappedItems);
        localStorage.setItem('cart', JSON.stringify(mappedItems));
      }
    } catch (err) {
      console.error("Cart sync failed", err);
    }
  };

  const addItem = async (item: CartItem) => {
    const newItems = [...items];
    const existing = newItems.find(i => i.id === item.id);
    if (existing) {
      existing.quantity += item.quantity;
    } else {
      newItems.push(item);
    }
    setItems(newItems);
    localStorage.setItem('cart', JSON.stringify(newItems));

    if (userId) {
      try {
        await apiCall(`/cart/${userId}/items`, {
          method: 'POST',
          body: JSON.stringify({
            productId: item.id,
            productName: item.name,
            price: item.price,
            quantity: item.quantity,
            imageUrl: item.image
          })
        });
      } catch (err) { console.error(err); }
    }
  };

  const removeItem = async (id: string) => {
    const newItems = items.filter(i => i.id !== id);
    setItems(newItems);
    localStorage.setItem('cart', JSON.stringify(newItems));

    if (userId) {
      try {
        await apiCall(`/cart/${userId}/items/${id}`, { method: 'DELETE' });
      } catch (err) { console.error(err); }
    }
  };

  const updateQuantity = async (id: string, quantity: number) => {
    if (quantity <= 0) {
      removeItem(id);
      return;
    }
    const newItems = items.map(i => i.id === id ? { ...i, quantity } : i);
    setItems(newItems);
    localStorage.setItem('cart', JSON.stringify(newItems));

    if (userId) {
      try {
        await apiCall(`/cart/${userId}/items/${id}?quantity=${quantity}`, { method: 'PUT' });
      } catch (err) { console.error(err); }
    }
  };

  const clearCart = async () => {
    setItems([]);
    localStorage.removeItem('cart');
    if (userId) {
      try {
        await apiCall(`/cart/${userId}`, { method: 'DELETE' });
      } catch (err) { console.error(err); }
    }
  };

  const totalItems = items.reduce((acc, i) => acc + i.quantity, 0);
  const totalPrice = items.reduce((acc, i) => acc + (i.price * i.quantity), 0);

  return (
    <CartContext.Provider value={{ items, addItem, removeItem, updateQuantity, clearCart, totalItems, totalPrice, syncCart }}>
      {children}
    </CartContext.Provider>
  );
}

export function useCart() {
  const context = useContext(CartContext);
  if (!context) throw new Error('useCart must be used within a CartProvider');
  return context;
}
