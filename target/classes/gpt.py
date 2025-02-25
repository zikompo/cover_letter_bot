import os
import sys
from openai import OpenAI

def generate_cover_letter(
    name: str,
    manager_name: str,
    company_name: str,
    job_title: str,
    skills: str,
    company_values: str
) -> str:
    """
    Generates a cover letter using GPT-3.5 based on the given parameters.

    :param name: Applicant's name
    :param manager_name: Hiring manager's name
    :param company_name: Company name
    :param job_title: Position to which the applicant is applying
    :param skills: Applicant's key skills/experience (comma-separated or a short description)
    :param company_values: Company values or mission statement
    :return: A 300-500 word cover letter as a string
    """
    client = OpenAI(api_key=os.getenv("OPENAI_API"))

    # The “system” message tells the model how to behave (the context/instructions).
    system_message = (
        "You are a chatbot that writes cover letters for users. "
        "You will be given the user's name, the manager name, the company name, the job title, "
        "the user's key skills, and the company's core values. "
        "You will then write a PROFESSIONAL cover letter for the user, "
        "300-500 words long, that highlights the user's skills and experience, "
        "explains how they align with the company's values, and is addressed to the hiring manager."
        "Do not add any placeholders. If something is not provided don't put it in the cover letter."
    )

    # The “user” message includes the prompt with placeholders injected.
    user_prompt = (
        f"Write a 300-500 word professional cover letter for a user named {name}, "
        f"addressed to {manager_name} at {company_name} for the position of {job_title}. "
        f"Highlight the user’s relevant skills and experience: {skills}, "
        f"and explain how they align with the company’s values: {company_values}. "
        "Use a polite and engaging tone."
    )

    # Create the chat completion
    response = client.chat.completions.create(
        model="gpt-3.5-turbo",
        messages=[
            {"role": "system", "content": system_message},
            {"role": "user", "content": user_prompt},
        ],
        temperature=0.7,
    )

    # Extract and return the assistant’s cover letter text
    cover_letter = response.choices[0].message.content
    print(cover_letter)
    return cover_letter

if __name__ == "__main__":
    # Expect 6 arguments in the order: name, manager_name, company_name, job_title, skills, company_values
    if len(sys.argv) < 7:
        print("Usage: python gpt.py <name> <manager_name> <company_name> <job_title> <skills> <company_values>")
        sys.exit(1)

    name = sys.argv[1]
    manager_name = sys.argv[2]
    company_name = sys.argv[3]
    job_title = sys.argv[4]
    skills = sys.argv[5]
    company_values = sys.argv[6]

    cover_letter = generate_cover_letter(name, manager_name, company_name, job_title, skills, company_values)
    print(cover_letter)