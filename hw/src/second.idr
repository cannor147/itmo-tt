data MyGTE : (left : Nat) -> (right : Nat) -> Type where
    MyGTEZero : MyGTE Z right
    MyGTESucc : MyGTE left right -> MyGTE (S left) (S right)

gte_1 : (MyGTE a b) -> (MyGTE b a) -> a = b
gte_1 MyGTEZero MyGTEZero = Refl
gte_1 (MyGTESucc a) (MyGTESucc b) = cong {f = \w => S w} (gte_1 a b)