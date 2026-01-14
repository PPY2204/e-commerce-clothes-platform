// Observer Pattern - Subject (Observable)
class OrderSubject {
  constructor() {
    this.observers = [];
  }

  attach(observer) {
    this.observers.push(observer);
  }

  detach(observer) {
    const index = this.observers.indexOf(observer);
    if (index > -1) {
      this.observers.splice(index, 1);
    }
  }

  notify(order, event) {
    this.observers.forEach(observer => {
      observer.update(order, event);
    });
  }
}

module.exports = OrderSubject;
