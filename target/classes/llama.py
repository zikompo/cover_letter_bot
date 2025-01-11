# Load model directly
from transformers import AutoTokenizer, AutoModelForCausalLM

tokenizer = AutoTokenizer.from_pretrained("meta-llama/LLaMA-2-7b-hf")
model = AutoModelForCausalLM.from_pretrained("meta-llama/LLaMA-2-7b-hf")

def generate_response(prompt):
    inputs = tokenizer(prompt, return_tensors="pt")
    outputs = model.generate(inputs["input_ids"], max_new_tokens=150, temperature=0.7)
    return tokenizer.decode(outputs[0], skip_special_tokens=True)

# Test the function
print(generate_response("Write a professional summary for a cover letter."))