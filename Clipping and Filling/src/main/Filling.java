package main;

import java.awt.Point;
import java.util.Vector;

import java.util.HashMap;


public class Filling {

	public Vector<Edge> edgelist;
	public Edge edgetable = null;
	public Edge activeedgetable = null;
	public HashMap<Integer, Edge> buckets;
	public int yMin;
	public int yMax;
	
	public Filling(Vector<Point> pointlist) {
		this.buckets = new HashMap<Integer, Edge>();
		this.edgelist = new Vector<Edge>();
		Point s = pointlist.lastElement();
		for(Point p : pointlist) {
			edgelist.add(new Edge(s, p));
			s = p;
		}	
		yMin = getMinY();
		yMax = getMaxY();
		Init();
		printET();
	}

	public void Init() {
		int edgesize = edgelist.size();
		Edge old = null, curEdge = edgetable;
		int curIndex = 0;
		int curYmin = 0;
		curEdge = getMinYp();
		curYmin = getMinY();
		curIndex = edgelist.indexOf(curEdge);
		edgelist.setElementAt(null, curIndex);
		edgesize--;
		old = curEdge;
		curEdge = getMinYp();
		if(curEdge == old) {
			edgelist.setElementAt(null, edgelist.indexOf(curEdge));
			edgesize--;
			if(old.xMax() < curEdge.xMax()) {
				Edge tmpE = old;
				old = curEdge;
				curEdge = tmpE;
			}
			curEdge.next = old;
			old = curEdge;
			buckets.put(curYmin, old);
		}
		else {
			old.next = buckets.get(curYmin);
			buckets.put(curYmin, old);
		}	
		while(edgesize != 0) {
			curEdge = getMinYp();
			curYmin = getMinY();
			curIndex = edgelist.indexOf(curEdge);
			edgelist.setElementAt(null, curIndex);
			edgesize--;
			old = curEdge;
			curEdge = getMinYp();
			if(curEdge == old) {
				edgelist.setElementAt(null, edgelist.indexOf(curEdge));
				edgesize--;
				if(old.xMax() < curEdge.xMax()) {
					Edge tmpE = old;
					old = curEdge;
					curEdge = tmpE;
				}
				curEdge.next = old;
				old = curEdge;
				buckets.put(curYmin, old);
			}
			else {
				old.next = buckets.get(curYmin);
				buckets.put(curYmin, old);
			}				
		}		
	}
	
	public int getMaxY() {
		int yMax = 0;
		for(Edge e : this.edgelist) {
			if(e != null)
				if(e.yMax > yMax)
					yMax = e.yMax;
		}
		return yMax;
	}
	
	public Edge getMaxYp() {
		int yMax = 0;
		Edge yM = null;
		for(Edge e : this.edgelist) {
			if(e != null)
				if(e.yMax > yMax) {
					yMax = e.yMax;
					yM = e;
				}
		}
		return yM;
	}
	
	public int getMinY() {
		int yMin = 100000;
		for(Edge e : this.edgelist) {
			if(e != null)
				if(e.getFirst().y < yMin || e.getSecond().y < yMin) 
					yMin = e.getFirst().y < e.getSecond().y ? e.getFirst().y:e.getSecond().y;			
			}
		return yMin;
	}
	
	public Edge getMinYp() {
		int yMin = 100000;
		Edge yM = null;
		for(Edge e : this.edgelist) {
			if(e != null)
				if(e.getFirst().y < yMin || e.getSecond().y < yMin) {
					yMin = e.getFirst().y < e.getSecond().y ? e.getFirst().y:e.getSecond().y;	
					yM = e;				
				}
		}
		return yM;
	}
	
	public void printET() {
		Edge e;
		for(int i=this.yMin; i <= this.yMax; i++) {
			if(!buckets.containsKey(i)) continue;
			e = buckets.get(i);
			System.out.print("Bucket "+i+": ");
			System.out.print("[(ymin: "+e.yMin()+"), (ymax:"+e.yMax+"), (xmin:"+e.xMin+"), (dx/dy:"+e.m+")");
			while(e.next != null) {
				e = e.next;
				System.out.print(" --> (ymin: "+e.yMin()+"), (ymax:"+e.yMax+"), (xmin:"+e.xMin+"), (dx/dy:"+e.m+")");
			}
			System.out.println("]");
		}
	}
	
	public int printAET() {
		Edge e = this.activeedgetable;
		int i =0;
		if(e == null) return 0;
		while(true) {
			if(e.next == null) {
				System.out.println("(yMax: "+e.yMax+"),(xMin: "+e.xMin+"), (m: "+e.m+")");
				i++;
				break;
			}
			System.out.println("(yMax: "+e.yMax+"),(xMin: "+e.xMin+"), (m: "+e.m+")");
			e = e.next;
			i++;
		}
		return i;
	}
	
	public void etToAet(int y) {
		Edge old;
		if(buckets.containsKey(y)) {
			if(activeedgetable == null) activeedgetable = buckets.get(y);
			else {
				old = activeedgetable;
				while(true) {
					if(old.next==null) {
						old.next = buckets.get(y); 
						break;
					}		
					old = old.next;
				}
			}
			buckets.remove(y);
		}
	}
	
	public void quickSort() {
		Vector<Edge> es = new Vector<Edge>();
		Edge e = this.activeedgetable;
		if(e == null) return;
		es.add(e);
		while(true) {
			if(e.next==null) {
				break;
			}
			e = e.next;
			es.add(e);
		}
		for(int i = 0; i < es.size()-1; i++) {
			for(int j = i+1; j < es.size(); j++) {
				if(es.get(j).xMin < es.get(i).xMin) {
					Edge tmp = es.get(j);
					es.set(j, es.get(i));
					es.set(i, tmp);
				}
			}
		}

		this.activeedgetable = es.firstElement();
		es.remove(0);
		e = this.activeedgetable;
		while(!es.isEmpty()) {
			e.next = es.firstElement();
			es.remove(0);
			e = e.next;
			e.next = null;
		}	
    }

	public void deleteNode(int x){
		Vector<Edge> es = new Vector<Edge>();
		Edge e = this.activeedgetable;
		if(e == null) return;
		System.out.println("Arrived");
		es.add(e);
		while(true) {
			if(e.next==null) {
				break;
			}
			e = e.next;
			es.add(e);
		}
		for(Edge d : es) {
			if(d.yMax == x)
				es.remove(es.indexOf(d));
		}
		this.activeedgetable = es.firstElement();
		es.remove(0);
		e = this.activeedgetable;
		while(!es.isEmpty()) {
			e.next = es.firstElement();
			es.remove(0);
			e = e.next;
			e.next = null;
		}
	}
//	public Edge dlNode(Edge head, int x) {
//		Edge p = head;
//		if(head == null) return head;
//		if(head.next == null) {
//			if(head.yMax == x) {
//				head = null;
//				return head;
//			}
//		}
//		Edge q = p.next;
//		while(q!=null) {
//			if(q.yMax == x) {
//				p.next = q.next;
//				p = q.next;
//				if(q.next == null) {
//					
//				}
//				q = p.next;
//				continue;
//			}
//			if(q.next == null) {
//				if(q.yMax == x) {
//					p.next = null;
//					break;
//				}
//			}
//			p = q;
//			q = q.next;
//		}
//		return head;
//	}
	public void plusEach() {
		Edge e = this.activeedgetable;
		if(e == null) return;
		while(true) {
			if(e.next == null) {
				e.xMin += e.m;
				break;
			}
			e.xMin += e.m;
			e = e.next;
		}
	}
	
	public int getIndexByValue(Edge head, int x) {
		Edge e = head;
		int i = 0;
		if(e == null) return -1;
		while(e != null) {
			if(e.yMax == x) {
				return i;
			} 
			e = e.next;
			i++;
		}
		return -1;
	}
	
	public Edge dlByIndex(Edge head, int index) {
		Edge p = head;
		Edge q = p;
		int i = 0;
		if(p == null) return head;
		if(index == 0) {
			head = head.next;
			return head;
		}
		while(i != index) {
			q = p;
			p = p.next;
			i++;
		}
		q.next = p.next;
		return head;
	}

	public class Edge{
		private Point point1;
		private Point point2;
		public int yMax;
		public float xMin;
		public float m;
		public Edge next;
		
		public Edge(Point point1, Point point2) {
			this.point1 = point1;
			this.point2 = point2;
			if(this.point1.y > this.point2.y) {
				this.yMax = this.point1.y;
				this.xMin = this.point2.x;
			}
			else {
				this.yMax = this.point2.y;
				this.xMin = this.point1.x;
			}
			this.m = (float) (1.0 * (this.point1.x - this.point2.x)/(this.point1.y - this.point2.y));
		}
		public Point getFirst() {
			return this.point1;
		}
		public Point getSecond() {
			return this.point2;
		}
		public int yMin() {
			return this.point1.y > this.point2.y ? this.point2.y : this.point1.y;
		}
		public int xMax() {
			return this.point1.y > this.point2.y ? this.point1.x : this.point2.x;
		}
		public boolean isInclude(Point point) {
			return point == point1 || point == point2;
		}
		public Point getYMaxPoint() {
			return point1.y > point2.y ? point1 : point2;
		}
		public Point getXMinPoint() {
			return point1.x > point2.x ? point2 : point1;
		}
		public Point getXMaxPoint() {
			return point1.x > point2.x ? point1 : point2;
		}		
	}
}
