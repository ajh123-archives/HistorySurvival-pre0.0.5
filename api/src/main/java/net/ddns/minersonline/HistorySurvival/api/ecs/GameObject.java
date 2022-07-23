package net.ddns.minersonline.HistorySurvival.api.ecs;

import java.util.ArrayList;
import java.util.List;

public class GameObject {
	private int id;
	private List<Component> components = new ArrayList<>();

	public GameObject() {}

	public <T extends Component> T getComponent(Class<T> componentClass){
		for (Component c : components){
			if(componentClass.isAssignableFrom(c.getClass())){
				try {
					return componentClass.cast(c);
				} catch (ClassCastException e){
					e.printStackTrace();
					assert false : "Error casting";
				}
			}
		}
		return null;
	}

	public <T extends Component> void removeComponent(Class<T> componentClass){
		for (int i=0; i < components.size(); i ++){
			if(componentClass.isAssignableFrom(components.get(i).getClass())){
				components.remove(i);
				return;
			}
		}
	}

	public void addComponent(Component c){
		components.add(c);
		c.gameObject = this;
	}

	public void update(float deltaTime){
		for (Component component : components) {
			component.update(deltaTime);
		}
	}

	public void start(){
		for (Component component : components) {
			component.start();
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
