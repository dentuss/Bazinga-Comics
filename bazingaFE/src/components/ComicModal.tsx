import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { ShoppingCart, Check, Heart } from "lucide-react";
import { useCart } from "@/contexts/CartContext";
import { useWishlist } from "@/contexts/WishlistContext";
import { useState } from "react";
import { useAuth } from "@/contexts/AuthContext";

interface ComicModalProps {
  isOpen: boolean;
  onClose: () => void;
  comic: {
    id: number;
    title: string;
    image: string;
    creators: string;
    year?: string;
    description?: string;
    price?: number;
    comicType?: string;
  };
}

const ComicModal = ({ isOpen, onClose, comic }: ComicModalProps) => {
  const { addToCart } = useCart();
  const { addToWishlist, removeFromWishlist, isInWishlist } = useWishlist();
  const { user } = useAuth();
  const [added, setAdded] = useState(false);
  const price = comic.price || 4.99;
  const subscriptionType = user?.subscriptionType?.toLowerCase();
  const isPremium = subscriptionType === "premium";
  const isUnlimited = subscriptionType === "unlimited";
  const isDigitalExclusive = comic.comicType === "ONLY_DIGITAL";
  const isDigitalRead =
    comic.comicType === "ONLY_DIGITAL" || comic.comicType === "PHYSICAL_COPY" || !comic.comicType;
  const isUnlimitedDigital = isUnlimited && isDigitalRead;
  const discountedPrice = isUnlimitedDigital ? 0 : isPremium ? price * 0.5 : price;
  const comicId = comic.id.toString();
  const inWishlist = isInWishlist(comicId);

  const handleAddToCart = () => {
    addToCart({
      id: comicId,
      title: comic.title,
      image: comic.image,
      creators: comic.creators,
      price,
    });
    setAdded(true);
    setTimeout(() => setAdded(false), 2000);
  };

  const handleWishlistToggle = () => {
    if (inWishlist) {
      removeFromWishlist(comicId);
    } else {
      addToWishlist({
        id: comicId,
        title: comic.title,
        image: comic.image,
        creators: comic.creators,
        price,
      });
    }
  };

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="max-w-2xl">
        <DialogHeader>
          <DialogTitle className="text-2xl font-bold">{comic.title}</DialogTitle>
        </DialogHeader>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <img
              src={comic.image}
              alt={comic.title}
              className="w-full rounded-lg shadow-lg"
            />
          </div>
          <div className="flex flex-col gap-4">
            <div className="flex flex-col gap-2">
              {isDigitalExclusive && (
                <span className="text-xs font-semibold uppercase text-yellow-500">Digital Exclusive</span>
              )}
              <div className="flex flex-wrap items-baseline gap-3">
                {isUnlimitedDigital ? (
                  <span className="text-2xl font-bold text-primary">FREE WITH UNLIMITED</span>
                ) : (
                  <span className="text-3xl font-bold text-primary">${discountedPrice.toFixed(2)}</span>
                )}
                {(isPremium || isUnlimitedDigital) && (
                  <span className="text-sm text-muted-foreground line-through">${price.toFixed(2)}</span>
                )}
              </div>
            </div>
            <div>
              <h3 className="text-sm font-semibold text-muted-foreground mb-1">CREATORS</h3>
              <p className="text-lg">{comic.creators}</p>
            </div>
            {comic.year && (
              <div>
                <h3 className="text-sm font-semibold text-muted-foreground mb-1">YEAR</h3>
                <p className="text-lg">{comic.year}</p>
              </div>
            )}
            <div>
              <h3 className="text-sm font-semibold text-muted-foreground mb-1">DESCRIPTION</h3>
              <p className="text-sm leading-relaxed">
                {comic.description || "An epic adventure awaits in this thrilling comic series. Join your favorite heroes as they battle against the forces of evil and protect the universe from destruction."}
              </p>
            </div>
            <div className="flex flex-col gap-2 mt-4">
              <Button 
                variant="default" 
                className="w-full"
                onClick={handleAddToCart}
                disabled={added}
              >
                {added ? (
                  <>
                    <Check className="h-4 w-4 mr-2" />
                    ADDED TO CART
                  </>
                ) : (
                  <>
                    <ShoppingCart className="h-4 w-4 mr-2" />
                    {isUnlimitedDigital ? "ADD TO CART - FREE" : `ADD TO CART - $${discountedPrice.toFixed(2)}`}
                  </>
                )}
              </Button>
              <Button 
                variant={inWishlist ? "secondary" : "outline"} 
                className="w-full"
                onClick={handleWishlistToggle}
              >
                <Heart className={`h-4 w-4 mr-2 ${inWishlist ? 'fill-current' : ''}`} />
                {inWishlist ? 'IN WISHLIST' : 'ADD TO WISHLIST'}
              </Button>
              <Button variant="ghost" className="w-full" onClick={onClose}>
                CONTINUE SHOPPING
              </Button>
            </div>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default ComicModal;
