export const FUNDIFY_FAQ_LIST = [
  {
    question: 'What is the Ris Synergy Network?',
    answer:
      'The Ris Synergy Network is a decentralized network for exchanging funding metadata.',
  },
  {
    question: 'What is FUNDify?',
    answer:
      'FUNDify is a comprehensive platform for managing and analyzing funding data within the RIS Synergy Network.',
  },
  {
    question: 'How do I add my own data?',
    answer:
      'You can add your own funding data through the frontend interface or via API endpoints.',
  },
  {
    question: 'Is my data secure?',
    answer: `Yes, we adhere to stringent security protocols to ensure all data is secure.`,
  },
  {
    question: 'How do I become a Ris Synergy Partner?',
    answer:
      'Simply sign up through our platform and follow the onboarding process.',
  },
  {
    question: 'Is FUNDify an open-source project?',
    answer:
      'FUNDify is an open-source project and you are welcomed to contribute. Visit our official GitHub repository for more information.',
  },
   {
    question: 'I cannot see the closed calls anymore',
    answer:
      'Check that the button "hide closed calls" in the Fundings section is deactivated.',
  },
] as const satisfies FAQ[];

type FAQ = { question: string; answer: string };
