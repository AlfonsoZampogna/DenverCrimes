package it.polito.tdp.crimes.model;

import java.util.ArrayList;
import java.util.List;


import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.crimes.db.Adiacenza;
import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	
	private EventsDao dao;
	private Graph<String,DefaultWeightedEdge> grafo;
    private List<String> best;

	
	public Model() {
		this.dao = new EventsDao();

	}
	
	public List<String> getCategorie(){
		return this.dao.getAllCategorie();
	}
	
	public List<Integer> mesi(){
		List<Integer> mesi = new ArrayList<Integer>();
		for(int i = 1 ; i< 13 ; i++) {
			mesi.add(i);
		}
		return mesi;
	}
	
	public void creaGrafo(String categoria, int mese) {
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		//aggiungo i vertici
		Graphs.addAllVertices(grafo, this.dao.getTipiReato(categoria, mese));
		
		//aggiungo gli archi
		for(Adiacenza a : this.dao.getArchi(categoria, mese)) {
			if(a.getPeso()>0) {
				Graphs.addEdgeWithVertices(this.grafo, a.getTipoReato1(), a.getTipoReato2(), a.getPeso());
			}
		}
		System.out.println("GRAFO CREATO!");
		System.out.println("# VERTICI : "+ this.grafo.vertexSet().size());
		System.out.println("# ARCHI : "+ this.grafo.edgeSet().size());
	}
	
	public int getNumeroVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int getNumeroArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public List<Adiacenza> getArchi(){
		List<Adiacenza> archi = new ArrayList<Adiacenza>();
		for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
			archi.add(new Adiacenza(this.grafo.getEdgeSource(e)
					,this.grafo.getEdgeTarget(e),(int) this.grafo.getEdgeWeight(e)));
		}
		return archi;
	}
	
	public List<Adiacenza> archiDaStampare(){
		double pesoTot = 0;
		double media = 0;
		for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
			pesoTot+=this.grafo.getEdgeWeight(e);
		}
		media = pesoTot/this.grafo.edgeSet().size();
		
		List<Adiacenza> archi = new ArrayList<Adiacenza>();
		for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
			if(this.grafo.getEdgeWeight(e)>=media)
				archi.add(new Adiacenza(this.grafo.getEdgeSource(e)
						,this.grafo.getEdgeTarget(e),(int) this.grafo.getEdgeWeight(e)));
		}
		return archi;
	}
	
	
	public List<String> calcolaPercorso2( Adiacenza arco){
		best = new ArrayList<String>();
		List<String> parziale = new ArrayList<String>();
		String partenza = arco.getTipoReato1();
		String arrivo = arco.getTipoReato2();
		parziale.add(partenza);
		run(arrivo,parziale);
		return best;
	}

	private void run(String arrivo, List<String> parziale) {
		// caso di terminazione
		if(parziale.get(parziale.size()-1).equals(arrivo)) {
			if(parziale.size()>best.size()) 
				best = new ArrayList<String>(parziale);
			return;
		}
		
		// recupero i vicini dell'ultimo vertice
		for(String s : Graphs.neighborListOf(grafo, parziale.get(parziale.size()-1))) {
			if(!parziale.contains(s)) {
				parziale.add(s);
			    run(arrivo,parziale);
			    parziale.remove(parziale.size()-1);
			}
		}
		
		
	}
}
