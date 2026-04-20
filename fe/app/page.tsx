import Image from "next/image";
import Link from "next/link";

export default function Home() {
  return (
    <main className="min-h-screen pt-16">
      {/* Hero Section */}
      <section className="relative h-[90vh] w-full flex items-center justify-center overflow-hidden">
        <div className="absolute inset-0 z-0">
          <div className="absolute inset-0 bg-gradient-to-r from-black/60 to-transparent z-10" />
          <Image 
            src="https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?q=80&w=2040&auto=format&fit=crop" 
            alt="Hero Background" 
            fill 
            className="object-cover"
            priority
          />
        </div>
        <div className="relative z-20 max-w-7xl mx-auto px-4 w-full">
          <h1 className="text-7xl md:text-9xl font-black text-white italic tracking-tighter leading-none mb-6">
            YAMATEE<br/>CLUB
          </h1>
          <p className="text-white text-lg md:text-xl font-medium max-w-lg mb-10 opacity-90">
            Pushing the boundaries of performance and style. Engineered for the next generation of athletes.
          </p>
          <div className="flex gap-4">
            <Link href="/shop" className="bg-white text-black px-10 py-4 rounded-full font-black hover:bg-gray-100 transition-all">
              SHOP NOW
            </Link>
            <Link href="/new" className="bg-transparent border-2 border-white text-white px-10 py-4 rounded-full font-black hover:bg-white/10 transition-all">
              NEW ARRIVALS
            </Link>
          </div>
        </div>
      </section>

      {/* Categories Grid */}
      <section className="py-24 max-w-7xl mx-auto px-4">
        <div className="flex items-end justify-between mb-12">
          <div>
            <h2 className="text-4xl font-black tracking-tighter italic">PICKLEBALL SERIES</h2>
            <p className="text-gray-500 font-medium">Elevate your game with our new collection</p>
          </div>
          <Link href="/collections" className="text-sm font-bold border-b-2 border-black pb-1">VIEW ALL</Link>
        </div>
        
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {[1, 2, 3, 4].map((i) => (
            <div key={i} className="group cursor-pointer">
              <div className="relative h-[400px] mb-4 bg-gray-100 rounded-3xl overflow-hidden">
                <Image 
                  src={`https://images.unsplash.com/photo-${1539106000000 + i}?auto=format&fit=crop&q=80&w=1000`} 
                  alt={`Product ${i}`} 
                  fill 
                  className="object-cover group-hover:scale-105 transition-transform duration-700"
                />
              </div>
              <h3 className="font-bold text-lg">PRO TECH TEE {i}</h3>
              <p className="text-gray-500 font-medium text-sm">$45.00</p>
            </div>
          ))}
        </div>
      </section>

      {/* FIFA Banner */}
      <section className="mx-4 mb-24 rounded-3xl h-[60vh] relative overflow-hidden flex items-center">
        <Image 
          src="https://images.unsplash.com/photo-1508098682722-e99c43a406b2?q=80&w=2070&auto=format&fit=crop" 
          alt="Banner" 
          fill 
          className="object-cover"
        />
        <div className="absolute inset-0 bg-blue-600/30 backdrop-blur-sm" />
        <div className="relative z-10 max-w-4xl mx-auto text-center px-4">
          <h2 className="text-5xl md:text-7xl font-black text-white italic tracking-tighter mb-6">FIFA WORLD CUP 2026</h2>
          <p className="text-white text-lg font-bold mb-10">Official Supporter Collection Available Now</p>
          <Link href="/shop" className="bg-white text-blue-600 px-10 py-4 rounded-full font-black hover:shadow-xl transition-all">
            GET THE KIT
          </Link>
        </div>
      </section>

      {/* Combo Co Ban */}
      <section className="py-24 bg-gray-50 overflow-hidden">
        <div className="max-w-7xl mx-auto px-4">
          <h2 className="text-4xl font-black tracking-tighter italic mb-12">COMBO CƠ BẢN</h2>
          <div className="flex flex-nowrap overflow-x-auto gap-8 pb-12 scrollbar-hide">
            {[1, 2, 3, 4, 5].map((i) => (
              <div key={i} className="min-w-[300px] bg-white p-6 rounded-3xl shadow-sm border border-gray-100">
                <div className="relative h-[200px] mb-6 rounded-2xl overflow-hidden">
                  <Image src={`https://images.unsplash.com/photo-1542291026-7eec264c27ff?q=80&w=1000&auto=format&fit=crop`} alt="Shoe" fill className="object-cover" />
                </div>
                <div className="flex justify-between items-center mb-4 text-xs font-bold text-gray-400 uppercase">
                  <span>New Bundle</span>
                  <span className="text-blue-600">Save 20%</span>
                </div>
                <h4 className="font-black text-xl mb-4 leading-tight">PERFORMANCE STARTER PACK {i}</h4>
                <div className="flex justify-between items-center">
                  <span className="font-bold text-gray-500">$120.00</span>
                  <button className="bg-black text-white px-4 py-2 rounded-xl text-xs font-bold">ADD TO CART</button>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>
    </main>
  );
}

