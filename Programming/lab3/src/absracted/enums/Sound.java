package absracted.enums;

public enum Sound {
    ENGINE_SOUND("мерный шум реактивного двигателя");

    private final String text;

    Sound(String text){
        this.text = text;
    }

    @Override
    public String toString(){
        return text;
    }
}
