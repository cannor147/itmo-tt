expr_1 : (x : Nat) -> (x * x) + (2 * x) + (2 * x) + (2 * 2) = (x * x) + (2 * x) + (2 * x) + (2 * 2)
expr_1 x = Refl
expr_2 : (x : Nat) -> (x * x) + (2 * x) + (x * 2) + (2 * 2) = (x * x) + (2 * x) + (2 * x) + (2 * 2)
expr_2 x = rewrite multCommutative x 2 in expr_1 x
expr_3 : (x : Nat) -> (x * x) + (2 * x) + ((x * 2) + (2 * 2)) = (x * x) + (2 * x) + (2 * x) + (2 * 2)
expr_3 x = rewrite plusAssociative ((x * x) + (2 * x)) (x * 2) (2 * 2) in expr_2 x
expr_4 : (x : Nat) -> (x * x) + (2 * x) + ((x + 2) * 2) = (x * x) + (2 * x) + (2 * x) + (2 * 2)
expr_4 x = rewrite multDistributesOverPlusLeft x 2 2 in expr_3 x
expr_5  : (x : Nat) -> ((x + 2) * x) + ((x + 2) * 2) = (x * x) + (2 * x) + (2 * x) + (2 * 2)
expr_5  x = rewrite multDistributesOverPlusLeft x 2 x in expr_4 x
expr_6 : (x : Nat) -> ((x + 2) * (x + 2)) = (x * x) + (2 * x) + (2 * x) + (2 * 2)
expr_6 x = rewrite multDistributesOverPlusRight (x + 2) x 2 in expr_5 x
expr_7 : (x : Nat) -> ((x + 2) * (x + 2)) = (x * x) + ((2 * x) + (2 * x)) + (2 * 2)
expr_7 x = rewrite plusAssociative (x * x) (2 * x) (2 * x) in expr_6 x
expr_8 : (x : Nat) -> ((x + 2) * (x + 2)) = (x * x) + (4 * x) + (2 * 2)
expr_8 x = rewrite multDistributesOverPlusLeft 2 2 x in expr_7 x
expr_9 : (x : Nat) -> (power (x + 2) 2) = (x * x) + (4 * x) + (2 * 2)
expr_9 x = rewrite powerSuccSuccMult (x + 2) in expr_8 x
expr_10 : (x : Nat) -> (power (x + 2) 2) = (power x 2) + (4 * x) + (2 * 2)
expr_10 x = rewrite powerSuccSuccMult x in expr_9 x