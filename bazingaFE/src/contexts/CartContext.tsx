import { createContext, useContext, useEffect, useState, ReactNode } from "react";
import { apiFetch } from "@/lib/api";
import { useAuth } from "./AuthContext";

export interface CartItem {
  id: string;
  title: string;
  image: string;
  creators: string;
  price: number;
  quantity: number;
  comicType?: string;
}

interface ApiCartItem {
  comic: {
    id: number;
    title: string;
    image: string;
    author?: string;
    price: number;
    comicType?: string;
  };
  comicId?: number;
  quantity: number;
}

interface CartContextType {
  items: CartItem[];
  addToCart: (item: Omit<CartItem, "quantity">) => void;
  removeFromCart: (id: string) => void;
  updateQuantity: (id: string, quantity: number) => void;
  clearCart: () => void;
  totalItems: number;
  totalPrice: number;
}

const CartContext = createContext<CartContextType | undefined>(undefined);

export const CartProvider = ({ children }: { children: ReactNode }) => {
  const { token } = useAuth();
  const [items, setItems] = useState<CartItem[]>([]);

  const mapApiItems = (response: ApiCartItem[]) =>
    response.map((item) => ({
      id: item.comic.id?.toString() || String(item.comicId),
      title: item.comic.title,
      image: item.comic.image,
      creators: item.comic.author || "",
      price: Number(item.comic.price),
      quantity: item.quantity,
      comicType: item.comic.comicType,
    }));

  useEffect(() => {
    const loadCart = async () => {
      if (!token) {
        setItems([]);
        return;
      }
      const response = await apiFetch<ApiCartItem[]>("/api/cart", { authToken: token });
      setItems(mapApiItems(response));
    };

    loadCart().catch(() => setItems([]));
  }, [token]);

  const addToCart = (item: Omit<CartItem, "quantity">) => {
    if (!token) return;
    apiFetch<ApiCartItem[]>("/api/cart", {
      method: "POST",
      authToken: token,
      body: JSON.stringify({ comicId: Number(item.id), quantity: 1 }),
    }).then((res) => {
      setItems(mapApiItems(res));
    });
  };

  const removeFromCart = (id: string) => {
    if (!token) return;
    apiFetch<ApiCartItem[]>(`/api/cart/${id}`, {
      method: "DELETE",
      authToken: token,
    }).then((res) => {
      setItems(mapApiItems(res));
    });
  };

  const updateQuantity = (id: string, quantity: number) => {
    if (!token) return;
    if (quantity <= 0) {
      removeFromCart(id);
      return;
    }
    apiFetch<ApiCartItem[]>("/api/cart", {
      method: "PUT",
      authToken: token,
      body: JSON.stringify({ comicId: Number(id), quantity }),
    }).then((res) => {
      setItems(mapApiItems(res));
    });
  };

  const clearCart = () => {
    if (!token) {
      setItems([]);
      return;
    }
    apiFetch<ApiCartItem[]>("/api/cart", {
      method: "DELETE",
      authToken: token,
    }).then((res) => {
      setItems(mapApiItems(res));
    });
  };

  const totalItems = items.reduce((sum, item) => sum + item.quantity, 0);
  const totalPrice = items.reduce((sum, item) => sum + item.price * item.quantity, 0);

  return (
    <CartContext.Provider
      value={{ items, addToCart, removeFromCart, updateQuantity, clearCart, totalItems, totalPrice }}
    >
      {children}
    </CartContext.Provider>
  );
};

export const useCart = () => {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error("useCart must be used within a CartProvider");
  }
  return context;
};
