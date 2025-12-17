import { Star } from "lucide-react";

interface ComicCardProps {
  image: string;
  title: string;
  creators?: string;
  rating?: number;
  onClick?: () => void;
}

const ComicCard = ({ image, title, creators, rating, onClick }: ComicCardProps) => {
  return (
    <div 
      onClick={onClick}
      className="group relative overflow-hidden rounded-sm bg-card transition-all duration-300 hover:-translate-y-2 hover:shadow-xl hover:shadow-primary/20 cursor-pointer"
    >
      <div className="aspect-[2/3] overflow-hidden">
        <img
          src={image}
          alt={title}
          className="h-full w-full object-cover transition-transform duration-300 group-hover:scale-110"
        />
      </div>
      <div className="p-3 space-y-1">
        <h3 className="font-bold text-sm line-clamp-2 group-hover:text-primary transition-colors">
          {title}
        </h3>
        {creators && (
          <p className="text-xs text-muted-foreground line-clamp-1">{creators}</p>
        )}
        {rating && (
          <div className="flex items-center gap-1">
            <Star className="h-3 w-3 fill-primary text-primary" />
            <span className="text-xs font-medium">{rating}</span>
          </div>
        )}
      </div>
    </div>
  );
};

export default ComicCard;
