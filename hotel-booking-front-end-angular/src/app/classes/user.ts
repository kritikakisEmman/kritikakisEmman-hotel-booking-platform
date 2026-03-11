export class User {
  id!: number;
  username!: string;
  email!: string;
  password!: string;
  roles!: {id: number, name: string}[];
}
