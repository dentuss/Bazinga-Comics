import { Facebook, Twitter, Instagram, Youtube, Twitch } from "lucide-react";

const Footer = () => {
  const links = [
    { label: "ABOUT BAZINGA", href: "#" },
    { label: "HELP/FAQS", href: "#" },
    { label: "CAREERS", href: "#" },
    { label: "INTERNSHIPS", href: "#" },
  ];

  const additionalLinks = [
    { label: "ADVERTISING", href: "#" },
    { label: "DISNEY+", href: "#" },
    { label: "MARVELHQ.COM", href: "#" },
    { label: "REDEEM DIGITAL COMICS", href: "#" },
  ];

  const socialLinks = [
    { icon: Facebook, href: "#", label: "Facebook" },
    { icon: Twitter, href: "#", label: "Twitter" },
    { icon: Instagram, href: "#", label: "Instagram" },
    { icon: Youtube, href: "#", label: "YouTube" },
    { icon: Twitch, href: "#", label: "Twitch" },
  ];

  const legalLinks = [
    "Terms of Use",
    "Privacy Policy",
    "Interest-Based Ads",
    "License Agreement",
    "Cookie Policy",
    "Your US State Privacy Rights",
    "©2025 BAZINGA",
  ];

  return (
    <footer className="border-t border-border bg-card">
      <div className="container mx-auto px-4 py-12">
        {/* Logo */}
        <div className="mb-8">
          <div className="text-4xl font-black text-primary">BAZINGA</div>
        </div>

        {/* Main Links */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8 mb-8">
          <div className="space-y-3">
            {links.map((link) => (
              <a
                key={link.label}
                href={link.href}
                className="block text-sm font-semibold text-foreground/80 hover:text-foreground transition-colors"
              >
                {link.label}
              </a>
            ))}
          </div>
          <div className="space-y-3">
            {additionalLinks.map((link) => (
              <a
                key={link.label}
                href={link.href}
                className="block text-sm font-semibold text-foreground/80 hover:text-foreground transition-colors"
              >
                {link.label}
              </a>
            ))}
          </div>
        </div>

        {/* Social Links */}
        <div className="mb-8">
          <h3 className="text-sm font-bold mb-4">FOLLOW BAZINGA</h3>
          <div className="flex gap-4">
            {socialLinks.map((social) => (
              <a
                key={social.label}
                href={social.href}
                className="w-10 h-10 flex items-center justify-center rounded-full bg-secondary hover:bg-primary hover:text-primary-foreground transition-colors"
                aria-label={social.label}
              >
                <social.icon className="h-5 w-5" />
              </a>
            ))}
          </div>
        </div>

        {/* Legal Links */}
        <div className="pt-8 border-t border-border">
          <div className="flex flex-wrap gap-x-4 gap-y-2 text-xs text-muted-foreground">
            {legalLinks.map((link, index) => (
              <span key={index}>
                {index < legalLinks.length - 1 ? (
                  <a href="#" className="hover:text-foreground transition-colors">
                    {link}
                  </a>
                ) : (
                  <span>{link}</span>
                )}
              </span>
            ))}
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
