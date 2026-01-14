// Base Product class
class Product {
  constructor(id, name, description, price, category) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.price = price;
    this.category = category;
    this.createdAt = new Date();
  }

  toJSON() {
    return {
      id: this.id,
      name: this.name,
      description: this.description,
      price: this.price,
      category: this.category,
      type: this.constructor.name,
      createdAt: this.createdAt
    };
  }
}

// Specialized product classes
class TShirt extends Product {
  constructor(id, name, description, price, size, color, material) {
    super(id, name, description, price, 'tshirt');
    this.size = size;
    this.color = color;
    this.material = material;
  }

  toJSON() {
    return {
      ...super.toJSON(),
      size: this.size,
      color: this.color,
      material: this.material
    };
  }
}

class Pants extends Product {
  constructor(id, name, description, price, size, color, waistSize, length) {
    super(id, name, description, price, 'pants');
    this.size = size;
    this.color = color;
    this.waistSize = waistSize;
    this.length = length;
  }

  toJSON() {
    return {
      ...super.toJSON(),
      size: this.size,
      color: this.color,
      waistSize: this.waistSize,
      length: this.length
    };
  }
}

class Jacket extends Product {
  constructor(id, name, description, price, size, color, material, waterproof) {
    super(id, name, description, price, 'jacket');
    this.size = size;
    this.color = color;
    this.material = material;
    this.waterproof = waterproof;
  }

  toJSON() {
    return {
      ...super.toJSON(),
      size: this.size,
      color: this.color,
      material: this.material,
      waterproof: this.waterproof
    };
  }
}

class Dress extends Product {
  constructor(id, name, description, price, size, color, style, length) {
    super(id, name, description, price, 'dress');
    this.size = size;
    this.color = color;
    this.style = style;
    this.length = length;
  }

  toJSON() {
    return {
      ...super.toJSON(),
      size: this.size,
      color: this.color,
      style: this.style,
      length: this.length
    };
  }
}

module.exports = { Product, TShirt, Pants, Jacket, Dress };
